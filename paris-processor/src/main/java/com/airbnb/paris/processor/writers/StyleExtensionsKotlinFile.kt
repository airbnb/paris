package com.airbnb.paris.processor.writers

import com.airbnb.paris.processor.*
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.framework.AndroidClassNames.ATTRIBUTE_SET
import com.airbnb.paris.processor.framework.AndroidClassNames.COLOR_INT
import com.airbnb.paris.processor.framework.AndroidClassNames.STYLE_RES
import com.airbnb.paris.processor.models.StyleableInfo
import com.squareup.kotlinpoet.*

/**
 * This generates a kt file with extension functions to style a specific view.
 *
 * For example, for ImageView this will generate a "ImageViewStyleExtensions.kt" file, with several
 * extensions on ImageView to apply a style.
 *
 * All the generated files must be in the same package so that "style" functions all use the same
 * import. Notably this leads to cleaner auto-complete dialogs.
 *
 * These extensions achieve the same effect as using the Paris class.
 *
 * TODO There could be a naming conflict if two @Styleable classes have the same name (but are in
 *      different packages)
 */
internal class StyleExtensionsKotlinFile(
        rClassName: KotlinClassName?,
        styleable: StyleableInfo
) : SkyKotlinFile(PARIS_KOTLIN_EXTENSIONS_PACKAGE_NAME, EXTENSIONS_FILE_NAME_FORMAT.format(styleable.elementName), {

    /**
     * An extension for setting a Style object on the view.
     *
     * Usage is like: "view.style(styleObject)"
     */
    function("style") {
        receiver(styleable.viewElementType)
        addParameter("style", STYLE_CLASS_NAME.toKPoet())
        addStatement("%T(this).apply(style)", styleable.styleApplierClassName().toKPoet())
    }

    /**
     * An extension for setting a style res on the view.
     *
     * Usage is like: "view.style(R.style.mystyle)"
     */
    function("style") {
        receiver(styleable.viewElementType)
        addParameter("styleRes", INT) {
            addAnnotation(STYLE_RES)
        }
        addStatement("%T(this).apply(styleRes)", styleable.styleApplierClassName().toKPoet())
    }

    /**
     * An extension for setting an AttributeSet on the view.
     *
     * Usage is like: "view.style(attrs)"
     */
    function("style") {
        receiver(styleable.viewElementType)
        addParameter("attrs", ATTRIBUTE_SET.toKPoet().asNullable())
        addStatement("%T(this).apply(attrs)", styleable.styleApplierClassName().toKPoet())
    }

    /**
     * An extension that applies a linked style.
     *
     * Eg: "view.styleGreen()"
     */
    styleable.styles.forEach {
        val styleName = it.formattedName

        function("style$styleName") {
            receiver(styleable.viewElementType)
            addStatement(
                    "%T(this).apply$styleName()",
                    styleable.styleApplierClassName().toKPoet()
            )
        }
    }

    /**
     * An extension for styling a view via a style builder.
     *
     * Usage is like: "view.style {  // builder as a receiver here  }"
     */
    function("style") {
        addModifiers(KModifier.INLINE)

        val viewTypeVariableName = KotlinTypeVariableName("V", styleable.viewElementType.asTypeName())
        addTypeVariable(viewTypeVariableName)
        receiver(viewTypeVariableName)

        val extendableStyleBuilderTypeName = KotlinParameterizedTypeName.get(
                EXTENDABLE_STYLE_BUILDER_CLASS_NAME.toKPoet(),
                viewTypeVariableName
        )
        val builderParameter = ParameterSpec.builder(
                "builder",
                LambdaTypeName.get(
                        receiver = extendableStyleBuilderTypeName,
                        returnType = UNIT
                )
        ).build()

        addParameter(builderParameter)

        addStatement(
                "%T(this).apply(%T().apply(%N).build())",
                styleable.styleApplierClassName().toKPoet(),
                extendableStyleBuilderTypeName,
                builderParameter
        )
    }

    val attrGroups = styleable.attrs.groupBy { it.styleableResId.resourceName }
    for (groupedAttrs in attrGroups.values) {
        rClassName!!

        val nonResTargetAttrs = groupedAttrs.filter { it.targetFormat != Format.RESOURCE_ID }

        val isTargetDimensionType = nonResTargetAttrs.any { it.targetFormat.isDimensionType }
        val isTargetColorStateListType = nonResTargetAttrs.any { it.targetFormat.isColorStateListType }

        val attr = if (nonResTargetAttrs.isNotEmpty()) nonResTargetAttrs.first() else groupedAttrs.first()
        val attrResourceName = attr.styleableResId.resourceName!!
        val baseMethodName = styleableAttrResourceNameToCamelCase(styleable.styleableResourceName, attrResourceName)

        if (nonResTargetAttrs.isNotEmpty()) {
            function(baseMethodName) {
                addKdoc(attr.javadoc.toKPoet())

                val extendableStyleBuilderTypeName = KotlinParameterizedTypeName.get(
                        EXTENDABLE_STYLE_BUILDER_CLASS_NAME.toKPoet(),
                        WildcardTypeName.subtypeOf(styleable.viewElementType.asTypeName())
                )
                receiver(extendableStyleBuilderTypeName)

                parameter("value", attr.targetType.asTypeName()) {
                    attr.targetFormat.valueAnnotation?.let {
                        addAnnotation(it)
                    }
                }

                addStatement("builder.put(%T.styleable.%L[%L], value)", rClassName, styleable.styleableResourceName, attr.styleableResId.code)
            }
        }

        function("${baseMethodName}Res") {
            addKdoc(attr.javadoc.toKPoet())

            val extendableStyleBuilderTypeName = KotlinParameterizedTypeName.get(
                    EXTENDABLE_STYLE_BUILDER_CLASS_NAME.toKPoet(),
                    WildcardTypeName.subtypeOf(styleable.viewElementType.asTypeName())
            )
            receiver(extendableStyleBuilderTypeName)

            parameter("resId", Integer.TYPE) {
                addAnnotation(attr.targetFormat.resAnnotation)
            }

            addStatement("builder.putRes(%T.styleable.%L[%L], resId)", rClassName, styleable.styleableResourceName, attr.styleableResId.code)
        }

        // Adds a special <attribute>Dp method that automatically converts a dp value to pixels for dimensions
        if (isTargetDimensionType) {
            function("${baseMethodName}Dp") {
                addKdoc(attr.javadoc.toKPoet())

                val extendableStyleBuilderTypeName = KotlinParameterizedTypeName.get(
                        EXTENDABLE_STYLE_BUILDER_CLASS_NAME.toKPoet(),
                        WildcardTypeName.subtypeOf(styleable.viewElementType.asTypeName())
                )
                receiver(extendableStyleBuilderTypeName)

                parameter("value", Integer.TYPE) {
                    addAnnotation(AnnotationSpec.builder(AndroidClassNames.DIMENSION.toKPoet())
                            .addMember("unit = %T.DP", AndroidClassNames.DIMENSION.toKPoet())
                            .build())
                }

                addStatement("builder.putDp(%T.styleable.%L[%L], value)", rClassName, styleable.styleableResourceName, attr.styleableResId.code)
            }
        }

        // Adds a special <attribute> method that automatically converts a @ColorInt to a ColorStateList
        if (isTargetColorStateListType) {
            function(baseMethodName) {
                addKdoc(attr.javadoc.toKPoet())

                val extendableStyleBuilderTypeName = KotlinParameterizedTypeName.get(
                        EXTENDABLE_STYLE_BUILDER_CLASS_NAME.toKPoet(),
                        WildcardTypeName.subtypeOf(styleable.viewElementType.asTypeName())
                )
                receiver(extendableStyleBuilderTypeName)

                parameter("color", Integer.TYPE) {
                    addAnnotation(COLOR_INT)
                }

                addStatement("builder.putColor(%T.styleable.%L[%L], color)", rClassName, styleable.styleableResourceName, attr.styleableResId.code)
            }
        }
    }

    /**
     * A helper (not an extension) for creating a Style object for the given view.
     *
     * Usage is like: "val style = imageViewStyle {  // builder as a receiver here  }"
     */
    function("${styleable.viewElementName.decapitalize()}Style") {
        addModifiers(KModifier.INLINE)
        returns(STYLE_CLASS_NAME.toKPoet())

        val builderParam = ParameterSpec.builder(
                "builder",
                LambdaTypeName.get(
                        receiver = styleable.styleBuilderClassName.toKPoet(),
                        returnType = UNIT
                )
        ).build()

        addParameter(builderParam)

        addStatement(
                "return %T().apply(%N).build()",
                styleable.styleBuilderClassName.toKPoet(),
                builderParam
        )
    }
})
