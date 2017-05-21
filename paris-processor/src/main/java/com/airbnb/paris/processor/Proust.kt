package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Format
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.utils.asTypeElement
import com.squareup.javapoet.*
import java.io.IOException
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Types

internal object Proust {

    private val CLASS_NAME_FORMAT = "%sStyleApplier"
    private val PARIS_CLASS_NAME = ClassName.get("com.airbnb.paris", "Paris")
    private val STYLE_CLASS_NAME = ClassName.get("com.airbnb.paris", "Style")
    private val STYLE_APPLIER_CLASS_NAME = ClassName.get("com.airbnb.paris", "StyleApplier")
    private val TYPED_ARRAY_WRAPPER_CLASS_NAME = ClassName.get("com.airbnb.paris", "TypedArrayWrapper")
    private val RESOURCES_CLASS_NAME = ClassName.get("android.content.res", "Resources")

    fun getClassName(classInfo: StyleableInfo): ClassName {
        return ClassName.get(classInfo.elementPackageName, String.format(CLASS_NAME_FORMAT, classInfo.elementName))
    }

    @Throws(IOException::class)
    fun writeFrom(filer: Filer, typeUtils: Types, styleableClasses: List<StyleableInfo>) {
        if (styleableClasses.isEmpty()) {
            return
        }

        val styleableClassesTree = StyleablesTree()

        for (styleableClassInfo in styleableClasses) {
            writeStyleClass(filer, typeUtils, styleableClasses, styleableClassInfo, styleableClassesTree)
        }
    }

    private fun writeStyleClass(filer: Filer, typeUtils: Types, styleableClasses: List<StyleableInfo>, classInfo: StyleableInfo, styleablesTree: StyleablesTree) {
        val className = getClassName(classInfo)

        val styleTypeBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(STYLE_APPLIER_CLASS_NAME, className, TypeName.get(classInfo.elementType)))
                .addMethod(buildConstructorMethod(classInfo))

        if (!classInfo.styleableResourceName.isEmpty()) {
            // Use an arbitrary AndroidResourceId to get R's ClassName. Per the StyleableInfo doc
            // it's safe to assume that attrs won't be empty if styleableResourceName isn't either
            val rClassName = classInfo.attrs[0].styleableResId.className!!.enclosingClassName()

            styleTypeBuilder
                    .addMethod(buildAttributesMethod(rClassName!!, classInfo.styleableResourceName))
                    .addMethod(buildProcessAttributesMethod(classInfo.attrs))
        }

        val parentStyleApplierClassName = styleablesTree.findStyleApplier(
                typeUtils,
                styleableClasses,
                typeUtils.asElement((typeUtils.asElement(classInfo.elementType) as TypeElement).superclass) as TypeElement)
        styleTypeBuilder.addMethod(buildApplyParentMethod(parentStyleApplierClassName))

        if (classInfo.dependencies.isNotEmpty()) {
            styleTypeBuilder.addMethod(buildApplyDependenciesMethod(classInfo))
        }

        for (attrInfo in classInfo.styleableAttrs) {
            val styleApplierClassName = styleablesTree.findStyleApplier(
                    typeUtils,
                    styleableClasses,
                    attrInfo.targetType.asTypeElement(typeUtils))
            styleTypeBuilder.addMethod(buildSubMethod(attrInfo, styleApplierClassName))
        }

        for (styleInfo in classInfo.styles) {
            styleTypeBuilder.addMethod(buildApplyStyleMethod(className, styleInfo))
        }

        JavaFile.builder(className.packageName(), styleTypeBuilder.build())
                .build()
                .writeTo(filer)
    }

    private fun buildConstructorMethod(classInfo: StyleableInfo): MethodSpec {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(classInfo.elementType), "view")
                .addStatement("super(view)")
                .build()
    }

    private fun buildApplyParentMethod(parentStyleApplierClassName: ClassName): MethodSpec {
        return MethodSpec.methodBuilder("applyParent")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ParameterSpec.builder(STYLE_CLASS_NAME, "style").build())
                .addStatement("new \$T(getView()).apply(style)", parentStyleApplierClassName)
                .build()
    }

    private fun buildApplyDependenciesMethod(classInfo: StyleableInfo): MethodSpec {
        val methodBuilder = MethodSpec.methodBuilder("applyDependencies")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ParameterSpec.builder(STYLE_CLASS_NAME, "style").build())

        for (dependency in classInfo.dependencies) {
            methodBuilder.addStatement("new \$T(getView()).apply(style)", dependency)
        }

        return methodBuilder.build()
    }

    private fun buildAttributesMethod(rClassName: ClassName, resourceName: String): MethodSpec {
        return MethodSpec.methodBuilder("attributes")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .returns(ArrayTypeName.of(Integer.TYPE))
                .addStatement("return \$T.styleable.\$L", rClassName, resourceName)
                .build()
    }

    private fun buildProcessAttributesMethod(attrs: List<AttrInfo>): MethodSpec {
        val methodSpecBuilder = MethodSpec.methodBuilder("processAttributes")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ParameterSpec.builder(STYLE_CLASS_NAME, "style").build())
                .addParameter(ParameterSpec.builder(TYPED_ARRAY_WRAPPER_CLASS_NAME, "a").build())
                .addStatement("\$T res = getView().getContext().getResources()", RESOURCES_CLASS_NAME)

        for (attr in attrs) {
            methodSpecBuilder.beginControlFlow("if (a.hasValue(\$L))", attr.styleableResId.code)
            addStatement(methodSpecBuilder, attr, "a", attr.targetFormat.typedArrayMethodStatement(), attr.styleableResId)
            methodSpecBuilder.endControlFlow()

            if (attr.defaultValueResId != null) {
                methodSpecBuilder.beginControlFlow("else")
                addStatement(methodSpecBuilder, attr, "res", attr.targetFormat.resourcesMethodStatement(), attr.defaultValueResId)
                methodSpecBuilder.endControlFlow()
            }
        }

        return methodSpecBuilder.build()
    }

    private fun addStatement(methodSpecBuilder: MethodSpec.Builder, attr: AttrInfo, from: String, statement: String, androidResourceId: AndroidResourceId) {
        if (attr.isElementStyleable) {
            assert(attr.targetFormat == Format.DEFAULT || attr.targetFormat == Format.RESOURCE_ID)
            methodSpecBuilder.addStatement("\$T.style(getView().\$N).apply($from.$statement)", PARIS_CLASS_NAME, attr.elementName, androidResourceId.code)
        } else if (attr.isElementAMethod) {
            methodSpecBuilder.addStatement("getView().\$N($from.$statement)", attr.elementName, androidResourceId.code)
        } else {
            methodSpecBuilder.addStatement("getView().\$N = $from.$statement", attr.elementName, androidResourceId.code)
        }
    }

    private fun buildSubMethod(attrInfo: AttrInfo, styleApplierClassName: ClassName): MethodSpec {
        return MethodSpec.methodBuilder(attrInfo.elementName)
                .addModifiers(Modifier.PUBLIC)
                .returns(styleApplierClassName)
                .addStatement("return new \$T(getView().\$N)", styleApplierClassName, attrInfo.elementName)
                .build()
    }

    private fun buildApplyStyleMethod(styleApplierClassName: ClassName, styleInfo: StyleInfo): MethodSpec {
        return MethodSpec.methodBuilder("apply${styleInfo.name.capitalize()}")
                .addModifiers(Modifier.PUBLIC)
                .returns(styleApplierClassName)
                .addStatement("return apply(\$L)", styleInfo.androidResourceId.code)
                .build()
    }
}
