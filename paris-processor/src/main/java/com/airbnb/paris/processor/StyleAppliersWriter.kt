package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Format
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.utils.ClassNames
import com.airbnb.paris.processor.utils.asTypeElement
import com.squareup.javapoet.*
import java.io.IOException
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.util.Types

internal object StyleAppliersWriter {

    @Throws(IOException::class)
    fun writeFrom(filer: Filer, typeUtils: Types, styleablesInfo: List<StyleableInfo>) {
        val styleableClassesTree = StyleablesTree()

        for (styleableInfo in styleablesInfo) {
            writeStyleApplier(filer, typeUtils, styleablesInfo, styleableInfo, styleableClassesTree)
        }
    }

    private fun writeStyleApplier(filer: Filer, typeUtils: Types, styleablesInfo: List<StyleableInfo>, styleableInfo: StyleableInfo, styleablesTree: StyleablesTree) {
        val styleApplierClassName = styleableInfo.styleApplierClassName()

        val styleTypeBuilder = TypeSpec.classBuilder(styleApplierClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(ParisProcessor.STYLE_APPLIER_CLASS_NAME, styleApplierClassName, TypeName.get(styleableInfo.elementType)))
                .addMethod(buildConstructorMethod(styleableInfo))

        if (!styleableInfo.styleableResourceName.isEmpty()) {
            // Use an arbitrary AndroidResourceId to get R's ClassName. Per the StyleableInfo doc
            // it's safe to assume that attrs won't be empty if styleableResourceName isn't either
            val rClassName = styleableInfo.attrs[0].styleableResId.className!!.enclosingClassName()

            styleTypeBuilder
                    .addMethod(buildAttributesMethod(rClassName!!, styleableInfo.styleableResourceName))
                    .addMethod(buildProcessAttributesMethod(styleableInfo.attrs))
        }

        val parentStyleApplierClassName = styleablesTree.findStyleApplier(
                typeUtils,
                styleablesInfo,
                styleableInfo.elementType.asTypeElement(typeUtils).superclass.asTypeElement(typeUtils))
        styleTypeBuilder.addMethod(buildApplyParentMethod(parentStyleApplierClassName))

        if (styleableInfo.dependencies.isNotEmpty()) {
            styleTypeBuilder.addMethod(buildApplyDependenciesMethod(styleableInfo))
        }

        for (attrInfo in styleableInfo.styleableAttrs) {
            val subStyleApplierClassName = styleablesTree.findStyleApplier(
                    typeUtils,
                    styleablesInfo,
                    attrInfo.targetType.asTypeElement(typeUtils))
            styleTypeBuilder.addMethod(buildSubMethod(attrInfo, subStyleApplierClassName))
        }

        for (styleInfo in styleableInfo.styles) {
            styleTypeBuilder.addMethod(buildApplyStyleMethod(styleApplierClassName, styleInfo))
        }

        JavaFile.builder(styleApplierClassName.packageName(), styleTypeBuilder.build())
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
                .addParameter(ParameterSpec.builder(ParisProcessor.STYLE_CLASS_NAME, "style").build())
                .addStatement("new \$T(getView()).apply(style)", parentStyleApplierClassName)
                .build()
    }

    private fun buildApplyDependenciesMethod(classInfo: StyleableInfo): MethodSpec {
        val methodBuilder = MethodSpec.methodBuilder("applyDependencies")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ParameterSpec.builder(ParisProcessor.STYLE_CLASS_NAME, "style").build())

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
        val methodBuilder = MethodSpec.methodBuilder("processAttributes")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ParameterSpec.builder(ParisProcessor.STYLE_CLASS_NAME, "style").build())
                .addParameter(ParameterSpec.builder(ParisProcessor.TYPED_ARRAY_WRAPPER_CLASS_NAME, "a").build())
                .addStatement("\$T res = getView().getContext().getResources()", ClassNames.ANDROID_RESOURCES)

        for (attr in attrs) {
            methodBuilder.beginControlFlow("if (a.hasValue(\$L))", attr.styleableResId.code)
            addStatement(methodBuilder, attr, "a", attr.targetFormat.typedArrayMethodStatement(), attr.styleableResId)
            methodBuilder.endControlFlow()

            if (attr.defaultValueResId != null) {
                methodBuilder.beginControlFlow("else")
                addStatement(methodBuilder, attr, "res", attr.targetFormat.resourcesMethodStatement(), attr.defaultValueResId)
                methodBuilder.endControlFlow()
            }
        }

        return methodBuilder.build()
    }

    private fun addStatement(methodSpecBuilder: MethodSpec.Builder, attr: AttrInfo, from: String, statement: String, androidResourceId: AndroidResourceId) {
        if (attr.isElementStyleable) {
            assert(attr.targetFormat == Format.DEFAULT || attr.targetFormat == Format.RESOURCE_ID)
            methodSpecBuilder.addStatement("\$T.style(getView().\$N).apply($from.$statement)", ParisProcessor.PARIS_CLASS_NAME, attr.elementName, androidResourceId.code)
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
