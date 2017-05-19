package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Format
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
    private val ATTRIBUTE_SET_CLASS_NAME = ClassName.get("android.util", "AttributeSet")
    private val TYPED_ARRAY_WRAPPER_CLASS_NAME = ClassName.get("com.airbnb.paris", "TypedArrayWrapper")
    private val CONFIG_CLASS_NAME = ClassName.get("com.airbnb.paris.Style", "Config")
    private val RESOURCES_CLASS_NAME = ClassName.get("android.content.res", "Resources")

    fun getClassName(classInfo: StyleableClassInfo): ClassName {
        return ClassName.get(classInfo.packageName, String.format(CLASS_NAME_FORMAT, classInfo.name))
    }

    @Throws(IOException::class)
    fun writeFrom(filer: Filer, typeUtils: Types, styleableClasses: List<StyleableClassInfo>, styleableClassesTree: StyleableClassesTree, rClassName: ClassName?) {
        if (styleableClasses.isEmpty()) {
            return
        }

        for (styleableClassInfo in styleableClasses) {
            writeStyleClass(filer, typeUtils, styleableClasses, styleableClassInfo, styleableClassesTree, rClassName)
        }
    }

    @Throws(IOException::class)
    private fun writeStyleClass(filer: Filer, typeUtils: Types, styleableClasses: List<StyleableClassInfo>, classInfo: StyleableClassInfo, styleableClassesTree: StyleableClassesTree, rClassName: ClassName?) {
        val className = getClassName(classInfo)

        val styleTypeBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(STYLE_APPLIER_CLASS_NAME, TypeName.get(classInfo.type)))
                .addMethod(buildConstructorMethod(classInfo))

        if (!classInfo.resourceName.isEmpty()) {
            // TODO  Error if no @Attrs is set but we have a resource name
            styleTypeBuilder
                    .addMethod(buildAttributesMethod(rClassName!!, classInfo.resourceName))
                    .addMethod(buildProcessAttributesMethod(classInfo.attrs))
        }

        val parentStyleApplierClassName = styleableClassesTree.findFirstStyleableSuperClassName(
                typeUtils,
                styleableClasses,
                typeUtils.asElement((typeUtils.asElement(classInfo.type) as TypeElement).superclass) as TypeElement)
        styleTypeBuilder.addMethod(buildApplyParentMethod(parentStyleApplierClassName))

        if (classInfo.dependencies.isNotEmpty()) {
            styleTypeBuilder.addMethod(buildApplyDependenciesMethod(classInfo))
        }

        for (attrInfo in classInfo.styleableAttrs) {
            val styleApplierClassName = styleableClassesTree.findFirstStyleableSuperClassName(
                    typeUtils,
                    styleableClasses,
                    typeUtils.asElement(attrInfo.type) as TypeElement)
            styleTypeBuilder.addMethod(buildSubMethod(attrInfo, styleApplierClassName))
        }

        JavaFile.builder(className.packageName(), styleTypeBuilder.build())
                .build()
                .writeTo(filer)
    }

    private fun buildConstructorMethod(classInfo: StyleableClassInfo): MethodSpec {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(classInfo.type), "view")
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

    private fun buildApplyDependenciesMethod(classInfo: StyleableClassInfo): MethodSpec {
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
            methodSpecBuilder.beginControlFlow("if (a.hasValue(\$L))", attr.id.code)
            addStatement(methodSpecBuilder, attr, "a", attr.format.typedArrayMethodStatement, attr.id)
            methodSpecBuilder.endControlFlow()

            if (attr.defaultValueResId != null) {
                methodSpecBuilder.beginControlFlow("else")
                addStatement(methodSpecBuilder, attr, "res", attr.format.resourcesMethodStatement, attr.defaultValueResId)
                methodSpecBuilder.endControlFlow()
            }
        }

        return methodSpecBuilder.build()
    }

    private fun addStatement(methodSpecBuilder: MethodSpec.Builder, attr: AttrInfo, from: String, statement: String, id: Id) {
        if (attr.isView) {
            assert(attr.format == Format.DEFAULT || attr.format == Format.RESOURCE_ID)
            methodSpecBuilder.addStatement("\$T.style(getView().\$N).apply($from.$statement)", PARIS_CLASS_NAME, attr.name, id.code)
        } else if (attr.isMethod) {
            methodSpecBuilder.addStatement("getView().\$N($from.$statement)", attr.name, id.code)
        } else {
            methodSpecBuilder.addStatement("getView().\$N = $from.$statement", attr.name, id.code)
        }
    }

    private fun buildSubMethod(attrInfo: AttrInfo, styleApplierClassName: ClassName): MethodSpec {
        return MethodSpec.methodBuilder(attrInfo.name)
                .addModifiers(Modifier.PUBLIC)
                .returns(styleApplierClassName)
                .addStatement("return new \$T(getView().\$N)", styleApplierClassName, attrInfo.name)
                .build()
    }

    private fun assert(assertion: Boolean) {
        // TODO
    }
}
