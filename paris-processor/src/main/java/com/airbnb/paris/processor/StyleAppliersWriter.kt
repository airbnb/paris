package com.airbnb.paris.processor

import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.utils.ClassNames
import com.airbnb.paris.processor.utils.VIEW_TYPE
import com.airbnb.paris.processor.utils.asTypeElement
import com.squareup.javapoet.*
import java.io.IOException
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

// TODO  Add @UiThread annotation to StyleApplier classes
internal object StyleAppliersWriter {

    @Throws(IOException::class)
    fun writeFrom(filer: Filer, elementUtils: Elements, typeUtils: Types, styleablesInfo: List<StyleableInfo>) {
        val styleableClassesTree = StyleablesTree()

        for (styleableInfo in styleablesInfo) {
            writeStyleApplier(filer, elementUtils, typeUtils, styleablesInfo, styleableInfo, styleableClassesTree)
        }
    }

    private fun writeStyleApplier(filer: Filer, elementUtils: Elements, typeUtils: Types, styleablesInfo: List<StyleableInfo>, styleableInfo: StyleableInfo, styleablesTree: StyleablesTree) {
        val styleApplierClassName = styleableInfo.styleApplierClassName()

        val styleTypeBuilder = TypeSpec.classBuilder(styleApplierClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(ParisProcessor.STYLE_APPLIER_CLASS_NAME, styleApplierClassName, TypeName.get(styleableInfo.elementType), TypeName.get(styleableInfo.viewElementType)))
                .addMethod(buildConstructorMethod(styleableInfo))

        if (!styleableInfo.styleableResourceName.isEmpty()) {
            // Use an arbitrary AndroidResourceId to get R's ClassName. Per the StyleableInfo doc
            // it's safe to assume that either styleableFields or attrs won't be empty if
            // styleableResourceName isn't either
            val arbitraryResId = if (!styleableInfo.styleableFields.isEmpty()) {
                styleableInfo.styleableFields[0].styleableResId
            } else {
                styleableInfo.attrs[0].styleableResId
            }
            val rClassName = arbitraryResId.className!!.enclosingClassName()

            styleTypeBuilder
                    .addMethod(buildAttributesMethod(rClassName!!, styleableInfo.styleableResourceName))
                    // TODO Only add if there are attributes with a default value?
                    .addMethod(buildAttributesWithDefaultValueMethod(styleableInfo.attrs))
                    .addMethod(buildProcessAttributesMethod(styleableInfo.styleableFields, styleableInfo.beforeStyles, styleableInfo.afterStyles, styleableInfo.attrs))
        }

        // If the view type is "View" then there is no parent
        if (!typeUtils.isSameType(elementUtils.VIEW_TYPE.asType(), styleableInfo.viewElementType)) {
            val parentStyleApplierClassName = styleablesTree.findStyleApplier(
                    typeUtils,
                    styleablesInfo,
                    styleableInfo.viewElementType.asTypeElement(typeUtils).superclass.asTypeElement(typeUtils))
            styleTypeBuilder.addMethod(buildApplyParentMethod(parentStyleApplierClassName))
        }

        if (styleableInfo.dependencies.isNotEmpty()) {
            styleTypeBuilder.addMethod(buildApplyDependenciesMethod(styleableInfo))
        }

        for (styleableFieldInfo in styleableInfo.styleableFields) {
            // TODO Enable @StyleableField for proxies? Why not
            val subStyleApplierClassName = styleablesTree.findStyleApplier(
                    typeUtils,
                    styleablesInfo,
                    styleableFieldInfo.elementType.asTypeElement(typeUtils))
            styleTypeBuilder.addMethod(buildSubMethod(styleableFieldInfo, subStyleApplierClassName))
        }

        for (styleInfo in styleableInfo.styles) {
            styleTypeBuilder.addMethod(buildApplyStyleMethod(styleApplierClassName, styleInfo))
        }

        JavaFile.builder(styleApplierClassName.packageName(), styleTypeBuilder.build())
                .build()
                .writeTo(filer)
    }

    private fun buildConstructorMethod(classInfo: StyleableInfo): MethodSpec {
        val builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(classInfo.viewElementType), "view")
        if (classInfo.elementType == classInfo.viewElementType) {
            builder.addStatement("super(view)")
        } else {
            // Different types means this style applier uses a proxy
            builder.addStatement("super(new \$T(view))", classInfo.elementType)
        }
        return builder.build()
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

    private fun buildAttributesWithDefaultValueMethod(attrs: List<AttrInfo>): MethodSpec {
        val builder = MethodSpec.methodBuilder("attributesWithDefaultValue")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .returns(ArrayTypeName.of(Integer.TYPE))
                .addCode("return new int[] {")
        val attrsWithDefaultValue = attrs.filter { it.defaultValueResId != null }
        for (attr in attrsWithDefaultValue) {
            builder.addCode("\$L,", attr.styleableResId.code)
        }
        return builder.addCode("};\n")
                .build()
    }

    private fun buildProcessAttributesMethod(styleableFields: List<StyleableFieldInfo>,
                                             beforeStyles: List<BeforeStyleInfo>,
                                             afterStyles: List<AfterStyleInfo>,
                                             attrs: List<AttrInfo>): MethodSpec {
        val methodBuilder = MethodSpec.methodBuilder("processAttributes")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ParameterSpec.builder(ParisProcessor.STYLE_CLASS_NAME, "style").build())
                .addParameter(ParameterSpec.builder(ParisProcessor.TYPED_ARRAY_WRAPPER_CLASS_NAME, "a").build())
                .addStatement("\$T res = getView().getContext().getResources()", ClassNames.ANDROID_RESOURCES)

        if (styleableFields.isNotEmpty()) {
            methodBuilder.addStatement("\$T subStyle", ParisProcessor.STYLE_CLASS_NAME)
        }

        for (beforeStyle in beforeStyles) {
            methodBuilder.addStatement("getProxy().\$N()", beforeStyle.elementName)
        }

        for (styleableField in styleableFields) {
            addControlFlow(methodBuilder, Format.RESOURCE_ID, styleableField.elementName,
                    styleableField.styleableResId, styleableField.defaultValueResId, true)
        }

        for (attr in attrs) {
            addControlFlow(methodBuilder, attr.targetFormat, attr.elementName,
                    attr.styleableResId, attr.defaultValueResId, false)
        }

        for (afterStyle in afterStyles) {
            methodBuilder.addStatement("getProxy().\$N()", afterStyle.elementName)
        }

        return methodBuilder.build()
    }

    private fun addControlFlow(methodBuilder: MethodSpec.Builder, format: Format,
                               elementName: String, styleableResId: AndroidResourceId,
                               defaultValueResId: AndroidResourceId?, isElementStyleable: Boolean) {
        methodBuilder.beginControlFlow("if (a.hasValue(\$L))", styleableResId.code)
        addStatement(methodBuilder, format, elementName, false, format.typedArrayMethodStatement(), styleableResId, isElementStyleable)
        methodBuilder.endControlFlow()

        if (defaultValueResId != null) {
            methodBuilder.beginControlFlow("else")
            addStatement(methodBuilder, format, elementName, true, format.resourcesMethodStatement(), defaultValueResId, isElementStyleable)
            methodBuilder.endControlFlow()
        }
    }

    private fun addStatement(methodSpecBuilder: MethodSpec.Builder, format: Format,
                             elementName: String, isForDefaultValue: Boolean, statement: String,
                             androidResourceId: AndroidResourceId, isElementStyleable: Boolean) {
        val from = if (isForDefaultValue) "res" else "a"
        if (isElementStyleable) {
            methodSpecBuilder
                    .addStatement("subStyle = new \$T($from.$statement)", ParisProcessor.STYLE_CLASS_NAME, androidResourceId.code)
                    .addStatement("subStyle.setDebugListener(style.getDebugListener())")
                    .addStatement("\$N().apply(subStyle)", elementName)
        } else {
            if (isForDefaultValue && format == Format.RESOURCE_ID) {
                // The parameter is the resource id
                methodSpecBuilder.addStatement("getProxy().\$N(\$L)", elementName, androidResourceId.code)
            } else {
                methodSpecBuilder.addStatement("getProxy().\$N($from.$statement)", elementName, androidResourceId.code)
            }
        }
    }

    private fun buildSubMethod(styleableFieldInfo: StyleableFieldInfo, styleApplierClassName: ClassName): MethodSpec {
        return MethodSpec.methodBuilder(styleableFieldInfo.elementName)
                .addModifiers(Modifier.PUBLIC)
                .returns(styleApplierClassName)
                .addStatement("return new \$T(getProxy().\$N)", styleApplierClassName, styleableFieldInfo.elementName)
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
