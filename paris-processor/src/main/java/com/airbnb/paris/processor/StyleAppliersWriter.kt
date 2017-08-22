package com.airbnb.paris.processor

import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.utils.ClassNames
import com.airbnb.paris.processor.utils.VIEW_TYPE
import com.airbnb.paris.processor.utils.asTypeElement
import com.squareup.javapoet.*
import java.io.IOException
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeKind
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

// TODO  Add @UiThread annotation to StyleApplier classes
internal object StyleAppliersWriter {

    val styleablesTree = StyleablesTree()

    lateinit var elementUtils: Elements
    lateinit var typeUtils: Types
    lateinit var styleablesInfo: List<StyleableInfo>

    @Throws(IOException::class)
    fun writeFrom(filer: Filer, elementUtils: Elements, typeUtils: Types, styleablesInfo: List<StyleableInfo>) {
        this.elementUtils = elementUtils
        this.typeUtils = typeUtils
        this.styleablesInfo = styleablesInfo

        for (styleableInfo in styleablesInfo) {
            writeStyleApplier(filer, elementUtils, typeUtils, styleableInfo)
        }
    }

    private fun writeStyleApplier(filer: Filer, elementUtils: Elements, typeUtils: Types, styleableInfo: StyleableInfo) {
        val styleApplierClassName = styleableInfo.styleApplierClassName()

        val styleTypeBuilder = TypeSpec.classBuilder(styleApplierClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(ParisProcessor.STYLE_APPLIER_CLASS_NAME, TypeName.get(styleableInfo.elementType), TypeName.get(styleableInfo.viewElementType)))
                .addMethod(buildConstructorMethod(styleableInfo))

        // If the view type is "View" then there is no parent
        var parentStyleApplierClassName: ClassName? = null
        if (!typeUtils.isSameType(elementUtils.VIEW_TYPE.asType(), styleableInfo.viewElementType)) {
            parentStyleApplierClassName = styleablesTree.findStyleApplier(
                    typeUtils,
                    styleablesInfo,
                    styleableInfo.viewElementType.asTypeElement(typeUtils).superclass.asTypeElement(typeUtils))
            styleTypeBuilder.addMethod(buildApplyParentMethod(parentStyleApplierClassName))
        }

        var rClassName: ClassName? = null
        if (!styleableInfo.styleableResourceName.isEmpty()) {
            // Use an arbitrary AndroidResourceId to get R's ClassName. Per the StyleableInfo doc
            // it's safe to assume that either styleableFields or attrs won't be empty if
            // styleableResourceName isn't either
            val arbitraryResId = if (!styleableInfo.styleableFields.isEmpty()) {
                styleableInfo.styleableFields[0].styleableResId
            } else {
                styleableInfo.attrs[0].styleableResId
            }
            rClassName = arbitraryResId.className!!.enclosingClassName()

            styleTypeBuilder
                    .addMethod(buildAttributesMethod(rClassName!!, styleableInfo.styleableResourceName))
                    // TODO Only add if there are attributes with a default value?
                    .addMethod(buildAttributesWithDefaultValueMethod(styleableInfo.attrs))
                    .addMethod(buildProcessStyleableFieldsMethod(styleableInfo.styleableFields))
                    .addMethod(buildProcessAttributesMethod(styleableInfo.beforeStyles, styleableInfo.afterStyles, styleableInfo.attrs))

        }

        val styleBuilderClassName = styleApplierClassName.nestedClass("StyleBuilder")
        addStyleBuilderInnerClass(styleTypeBuilder, styleApplierClassName, rClassName, styleableInfo, parentStyleApplierClassName)

        for (styleableFieldInfo in styleableInfo.styleableFields) {
            // TODO Enable @StyleableField for proxies? Why not
            val subStyleApplierClassName = styleablesTree.findStyleApplier(
                    typeUtils,
                    styleablesInfo,
                    styleableFieldInfo.elementType.asTypeElement(typeUtils))
            styleTypeBuilder.addMethod(buildSubMethod(styleableFieldInfo, subStyleApplierClassName))
        }

        for (styleInfo in styleableInfo.styles) {
            styleTypeBuilder.addMethod(buildApplyStyleMethod(styleInfo))
        }

        for (styleInfo in styleableInfo.newStyles) {
            styleTypeBuilder.addMethod(buildApplyNewStyleMethod(styleBuilderClassName, styleInfo))
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

    private fun buildProcessStyleableFieldsMethod(styleableFields: List<StyleableFieldInfo>): MethodSpec {
        val methodBuilder = MethodSpec.methodBuilder("processStyleableFields")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ParameterSpec.builder(ParisProcessor.STYLE_CLASS_NAME, "style").build())
                .addParameter(ParameterSpec.builder(ParisProcessor.TYPED_ARRAY_WRAPPER_CLASS_NAME, "a").build())
                .addStatement("\$T res = getView().getContext().getResources()", ClassNames.ANDROID_RESOURCES)

        if (styleableFields.isNotEmpty()) {
            methodBuilder.addStatement("\$T subStyle", ParisProcessor.STYLE_CLASS_NAME)
        }

        for (styleableField in styleableFields) {
            addControlFlow(methodBuilder, Format.STYLE, styleableField.elementName,
                    styleableField.styleableResId, styleableField.defaultValueResId, true)
        }

        return methodBuilder.build()
    }

    private fun buildProcessAttributesMethod(beforeStyles: List<BeforeStyleInfo>,
                                             afterStyles: List<AfterStyleInfo>,
                                             attrs: List<AttrInfo>): MethodSpec {
        val methodBuilder = MethodSpec.methodBuilder("processAttributes")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ParameterSpec.builder(ParisProcessor.STYLE_CLASS_NAME, "style").build())
                .addParameter(ParameterSpec.builder(ParisProcessor.TYPED_ARRAY_WRAPPER_CLASS_NAME, "a").build())
                .addStatement("\$T res = getView().getContext().getResources()", ClassNames.ANDROID_RESOURCES)

        for (beforeStyle in beforeStyles) {
            methodBuilder.addStatement("getProxy().\$N(style)", beforeStyle.elementName)
        }

        for (attr in attrs) {
            addControlFlow(methodBuilder, attr.targetFormat, attr.elementName,
                    attr.styleableResId, attr.defaultValueResId, false)
        }

        for (afterStyle in afterStyles) {
            methodBuilder.addStatement("getProxy().\$N(style)", afterStyle.elementName)
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
                    .addStatement("subStyle = $from.$statement", androidResourceId.code)
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

    private fun buildApplyStyleMethod(styleInfo: StyleInfo): MethodSpec {
        return MethodSpec.methodBuilder("apply${styleInfo.name.capitalize()}")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("apply(\$L)", styleInfo.androidResourceId.code)
                .build()
    }

    private fun buildApplyNewStyleMethod(styleBuilderClassName: ClassName, styleInfo: NewStyleInfo): MethodSpec {
        val builder = MethodSpec.methodBuilder("apply${styleInfo.elementName.capitalize()}")
                .addModifiers(Modifier.PUBLIC)
        when (styleInfo.elementKind) {
            NewStyleInfo.Kind.FIELD -> {
                builder.addStatement("apply(\$T.\$L)", styleInfo.enclosingElement, styleInfo.elementName)
            }
            NewStyleInfo.Kind.METHOD -> {
                builder.addStatement("\$T builder = new \$T()", styleBuilderClassName, styleBuilderClassName)
                        .addStatement("\$T.\$L(builder)", styleInfo.enclosingElement, styleInfo.elementName)
                        .addStatement("apply(builder.build())")
            }
        }
        return builder.build()
    }

    private fun addStyleBuilderInnerClass(styleApplierTypeBuilder: TypeSpec.Builder,
                                          styleApplierClassName: ClassName,
                                          rClassName: ClassName?,
                                          styleableInfo: StyleableInfo,
                                          parentStyleApplierClassName: ClassName?) {
        // BaseStyleBuilder inner class
        val baseStyleBuilderClassName: ClassName?
        if (parentStyleApplierClassName != null) {
            baseStyleBuilderClassName = parentStyleApplierClassName.nestedClass("BaseStyleBuilder")
        } else {
            baseStyleBuilderClassName = ParisProcessor.STYLE_BUILDER_CLASS_NAME
        }
        val wildcardTypeName = WildcardTypeName.subtypeOf(Object::class.java)
        val baseClassName = ClassName.get(styleApplierClassName.packageName(), styleApplierClassName.simpleName(), "BaseStyleBuilder")
        val baseStyleBuilderTypeBuilder = TypeSpec.classBuilder(baseClassName)
                .addTypeVariable(TypeVariableName.get("B", ParameterizedTypeName.get(baseClassName, TypeVariableName.get("B"), TypeVariableName.get("A"))))
                .addTypeVariable(TypeVariableName.get("A", ParameterizedTypeName.get(ParisProcessor.STYLE_APPLIER_CLASS_NAME, wildcardTypeName, wildcardTypeName)))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT)
                .superclass(ParameterizedTypeName.get(baseStyleBuilderClassName, TypeVariableName.get("B"), TypeVariableName.get("A")))
                .addMethod(buildStyleBuilderApplierConstructorMethod(TypeVariableName.get("A")))
                .addMethod(buildStyleBuilderEmptyConstructorMethod())

        // TODO Remove duplicate attribute names
        for (styleableFieldInfo in styleableInfo.styleableFields) {
            baseStyleBuilderTypeBuilder.addMethod(buildStyleBuilderAddSubResMethod(styleableInfo.styleableResourceName, styleableFieldInfo))
            baseStyleBuilderTypeBuilder.addMethod(buildStyleBuilderAddSubMethod(styleableInfo.styleableResourceName, styleableFieldInfo))
            baseStyleBuilderTypeBuilder.addMethod(buildStyleBuilderAddSubBuilderMethod(styleableInfo.styleableResourceName, styleableFieldInfo))
        }

        val distinctAttrs = styleableInfo.attrs.distinctBy { it.styleableResId.resourceName }
        distinctAttrs.mapNotNull { buildAttributeSetterResMethod(rClassName!!, styleableInfo.styleableResourceName, it) }
                .forEach { baseStyleBuilderTypeBuilder.addMethod(it) }
        distinctAttrs.mapNotNull { buildAttributeSetterMethod(rClassName!!, styleableInfo.styleableResourceName, it) }
                .forEach { baseStyleBuilderTypeBuilder.addMethod(it) }

        baseStyleBuilderTypeBuilder.addMethod(buildApplyToMethod(styleableInfo, styleApplierClassName))

        styleApplierTypeBuilder.addType(baseStyleBuilderTypeBuilder.build())

        // StyleBuilder inner class
        val styleBuilderClassName = styleApplierClassName.nestedClass("StyleBuilder")
        val styleBuilderTypeBuilder = TypeSpec.classBuilder(styleBuilderClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(baseClassName, styleBuilderClassName, styleApplierClassName))
                .addMethod(buildStyleBuilderApplierConstructorMethod(styleApplierClassName))
                .addMethod(buildStyleBuilderEmptyConstructorMethod())

        styleableInfo.styles.forEach {
            styleBuilderTypeBuilder.addMethod(buildStyleBuilderAddMethod(styleBuilderClassName, it))
        }
        styleableInfo.newStyles.forEach {
            styleBuilderTypeBuilder.addMethod(buildNewStyleBuilderAddMethod(styleBuilderClassName, it))
        }

        styleApplierTypeBuilder.addType(styleBuilderTypeBuilder.build())

        // builder() method
        styleApplierTypeBuilder.addMethod(MethodSpec.methodBuilder("builder")
                .addModifiers(Modifier.PUBLIC)
                .returns(styleBuilderClassName)
                .addStatement("return new \$T(this)", styleBuilderClassName)
                .build())
    }

    private fun buildStyleBuilderApplierConstructorMethod(parameterTypeName: TypeName): MethodSpec {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterTypeName, "applier")
                .addStatement("super(applier)")
                .build()
    }

    private fun buildStyleBuilderEmptyConstructorMethod(): MethodSpec {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build()
    }

    private fun buildStyleBuilderAddMethod(styleBuilderClassName: ClassName, styleInfo: StyleInfo): MethodSpec {
        return MethodSpec.methodBuilder("add${styleInfo.name.capitalize()}")
                .addModifiers(Modifier.PUBLIC)
                .returns(styleBuilderClassName)
                .addStatement("add(\$L)", styleInfo.androidResourceId.code)
                .addStatement("return this")
                .build()
    }

    private fun buildNewStyleBuilderAddMethod(styleBuilderClassName: ClassName, styleInfo: NewStyleInfo): MethodSpec {
        val builder = MethodSpec.methodBuilder("add${styleInfo.elementName.capitalize()}")
                .addModifiers(Modifier.PUBLIC)
                .returns(styleBuilderClassName)
        when (styleInfo.elementKind) {
            NewStyleInfo.Kind.FIELD -> builder.addStatement("add(\$T.\$L)", styleInfo.enclosingElement, styleInfo.elementName)
            NewStyleInfo.Kind.METHOD -> builder.addStatement("\$T.\$L(this)", styleInfo.enclosingElement, styleInfo.elementName)
        }
        return builder
                .addStatement("return this")
                .build()
    }

    private fun buildStyleBuilderAddSubResMethod(styleableResourceName: String, styleableFieldInfo: StyleableFieldInfo): MethodSpec {
        return MethodSpec.methodBuilder(styleableAttrResourceNameToCamelCase(styleableResourceName, styleableFieldInfo.styleableResId.resourceName!!))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(Integer.TYPE, "value").build())
                .returns(TypeVariableName.get("B"))
                .addStatement("getBuilder().put(\$L, value)", styleableFieldInfo.styleableResId.code)
                .addStatement("return (B) this")
                .build()
    }

    private fun buildStyleBuilderAddSubMethod(styleableResourceName: String, styleableFieldInfo: StyleableFieldInfo): MethodSpec {
        return MethodSpec.methodBuilder(styleableAttrResourceNameToCamelCase(styleableResourceName, styleableFieldInfo.styleableResId.resourceName!!))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ParisProcessor.STYLE_CLASS_NAME, "value").build())
                .returns(TypeVariableName.get("B"))
                .addStatement("getBuilder().put(\$L, value)", styleableFieldInfo.styleableResId.code)
                .addStatement("return (B) this")
                .build()
    }

    private fun buildStyleBuilderAddSubBuilderMethod(styleableResourceName: String, styleableFieldInfo: StyleableFieldInfo): MethodSpec {
        val styleApplierClassName = styleablesTree.findStyleApplier(
                typeUtils,
                styleablesInfo,
                styleableFieldInfo.elementType.asTypeElement(typeUtils))
        val styleBuilderClassName = styleApplierClassName.nestedClass("StyleBuilder")
        return MethodSpec.methodBuilder(styleableAttrResourceNameToCamelCase(styleableResourceName, styleableFieldInfo.styleableResId.resourceName!!))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(Function1::class.java), styleBuilderClassName, TypeName.get(Void::class.java)), "function").build())
                .returns(TypeVariableName.get("B"))
                .addStatement("\$T subBuilder = new \$T()", styleBuilderClassName, styleBuilderClassName)
                .addStatement("function.invoke(subBuilder)")
                .addStatement("getBuilder().put(\$L, subBuilder.build())", styleableFieldInfo.styleableResId.code)
                .addStatement("return (B) this")
                .build()
    }

    private fun buildAttributeSetterResMethod(rClassName: ClassName, styleableResourceName: String, attr: AttrInfo): MethodSpec? {
        val attrResourceName = attr.styleableResId.resourceName
        if (attrResourceName != null) {
            var methodName = styleableAttrResourceNameToCamelCase(styleableResourceName, attrResourceName)
            if (TypeKind.INT == attr.targetType.kind) {
                methodName += "Res"
            }
            return MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(Integer.TYPE, "resId")
                            .addAnnotation(ClassNames.ANDROID_ANY_RES)
                            .build())
                    .returns(TypeVariableName.get("B"))
                    .addStatement("getBuilder().put(\$T.styleable.\$L[\$L], resId)", rClassName, styleableResourceName, attr.styleableResId.code)
                    .addStatement("return (B) this")
                    .build()
        } else {
            return null
        }
    }

    private fun buildAttributeSetterMethod(rClassName: ClassName, styleableResourceName: String, attr: AttrInfo): MethodSpec? {
        val attrResourceName = attr.styleableResId.resourceName
        if (attrResourceName != null) {
            val methodName = styleableAttrResourceNameToCamelCase(styleableResourceName, attrResourceName)
            return MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(TypeName.get(attr.targetType), "value").build())
                    .returns(TypeVariableName.get("B"))
                    .addStatement("getBuilder().put(\$T.styleable.\$L[\$L], value)", rClassName, styleableResourceName, attr.styleableResId.code)
                    .addStatement("return (B) this")
                    .build()
        } else {
            return null
        }
    }

    private fun buildApplyToMethod(styleableInfo: StyleableInfo, styleApplierClassName: ClassName): MethodSpec? {
        val methodBuilder = MethodSpec.methodBuilder("applyTo")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(TypeName.get(styleableInfo.viewElementType), "view").build())
                .returns(TypeVariableName.get("B"))
                .addStatement("new \$T(view).apply(build())", styleApplierClassName)
                .addStatement("return (B) this")
        return methodBuilder.build()
    }

    /**
     * Applies lower camel case formatting
     */
    private fun styleableAttrResourceNameToCamelCase(styleableResourceName: String, name: String): String {
        var formattedName = name.removePrefix("${styleableResourceName}_")
        formattedName = formattedName.removePrefix("android_")
        formattedName = formattedName.foldRightIndexed("") { index, c, acc ->
            if (c == '_') {
                acc
            } else {
                if (index == 0 || formattedName[index - 1] != '_') {
                    c + acc
                } else {
                    c.toUpperCase() + acc
                }
            }
        }
        formattedName = formattedName.first().toLowerCase() + formattedName.drop(1)
        return formattedName
    }
}
