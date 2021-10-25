package com.airbnb.paris.processor.writers

import androidx.annotation.RequiresApi
import androidx.room.compiler.processing.addOriginatingElement
import com.airbnb.paris.processor.EXTENDABLE_STYLE_BUILDER_CLASS_NAME
import com.airbnb.paris.processor.EXTENSIONS_FILE_NAME_FORMAT
import com.airbnb.paris.processor.Format
import com.airbnb.paris.processor.PARIS_KOTLIN_EXTENSIONS_PACKAGE_NAME
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_CLASS_NAME
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.framework.AndroidClassNames.ATTRIBUTE_SET
import com.airbnb.paris.processor.framework.AndroidClassNames.COLOR_INT
import com.airbnb.paris.processor.framework.AndroidClassNames.STYLE_RES
import com.airbnb.paris.processor.models.AttrInfo
import com.airbnb.paris.processor.models.StyleableInfo

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.WildcardTypeName

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
    processor: ParisProcessor,
    styleable: StyleableInfo
) : SkyKotlinFile(processor) {

    override val packageName = PARIS_KOTLIN_EXTENSIONS_PACKAGE_NAME
    override val name = EXTENSIONS_FILE_NAME_FORMAT.format(styleable.viewElementName)

    override val block: FileSpec.Builder.() -> Unit = {

        addAnnotation(
            AnnotationSpec.builder(Suppress::class)
                // We suppress Deprecation warnings for this class in case any of the models used are deprecated.
                // This prevents the generated file from causing errors for using deprecated classes.
                .addMember("%S", "DEPRECATION")
                // Similarly we suppress the max line length warning since readability isn't so much an issue with generated files and there's no
                // easy fix.
                .addMember("%S", "Detekt.MaxLineLength")
                .build()
        )

        /*
         * An extension for setting a Style object on the view.
         *
         * Usage is like: "view.style(styleObject)"
         */
        function("style") {
            receiver(styleable.viewElementType)
            addParameter("style", STYLE_CLASS_NAME.toKPoet())
            addStatement("%T(this).apply(style)", styleable.styleApplierClassName.toKPoet())
            addOriginatingElement(styleable.annotatedElement)
        }

        /*
         * An extension for setting a style res on the view.
         *
         * Usage is like: "view.style(R.style.mystyle)"
         */
        function("style") {
            receiver(styleable.viewElementType)
            addParameter("styleRes", INT) {
                addAnnotation(STYLE_RES)
            }
            addStatement("%T(this).apply(styleRes)", styleable.styleApplierClassName.toKPoet())
            addOriginatingElement(styleable.annotatedElement)
        }

        /*
         * An extension for setting an AttributeSet on the view.
         *
         * Usage is like: "view.style(attrs)"
         */
        function("style") {
            receiver(styleable.viewElementType)
            addParameter("attrs", ATTRIBUTE_SET.toKPoet().copy(nullable = true))
            addStatement("%T(this).apply(attrs)", styleable.styleApplierClassName.toKPoet())
            addOriginatingElement(styleable.annotatedElement)
        }

        /*
         * An extension for styling a view via a style builder.
         *
         * Usage is like: "view.style {  // builder as a receiver here  }"
         */
        function("style") {
            addModifiers(KModifier.INLINE)

            val viewTypeName: KotlinTypeName
            if (styleable.viewElement.isFinal()) {
                viewTypeName = styleable.viewElement.type.typeNameKotlin()
            } else {
                // If the styleable class isn't final we use generics so that subclasses are able to override this extension function
                viewTypeName = KotlinTypeVariableName("V", styleable.viewElementType.typeNameKotlin())
                addTypeVariable(viewTypeName)
            }

            receiver(viewTypeName)

            val extendableStyleBuilderTypeName = EXTENDABLE_STYLE_BUILDER_CLASS_NAME.toKPoet().parameterizedBy(viewTypeName)
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
                styleable.styleApplierClassName.toKPoet(),
                extendableStyleBuilderTypeName,
                builderParameter
            )

            addOriginatingElement(styleable.annotatedElement)
        }

        /*
         * Style builder extensions to add linked styles.
         *
         * These are purposefully not available to style builders of subclasses.
         *
         * Usage is like: "view.style { addDefault() }"
         */
        styleable.styles.forEach {
            function("add${it.formattedName}") {
                addKdoc(it.kdoc)

                val extendableStyleBuilderTypeName = EXTENDABLE_STYLE_BUILDER_CLASS_NAME.toKPoet().parameterizedBy(
                    styleable.viewElementType.typeNameKotlin()
                )
                receiver(extendableStyleBuilderTypeName)

                addStatement(
                    "add(%T().add${it.formattedName}().build())",
                    styleable.styleBuilderClassName.toKPoet()
                )

                addOriginatingElement(styleable.annotatedElement)
            }
        }

        val extendableStyleBuilderTypeName = EXTENDABLE_STYLE_BUILDER_CLASS_NAME.toKPoet().parameterizedBy(
            WildcardTypeName.producerOf(styleable.viewElementType.typeNameKotlin())
        )

        /*
         * Style builder extensions to set sub-styles.
         *
         * These are available to style builders of subclasses as well.
         *
         * Usage is like: "myStyle.style { titleStyle(style) }"
         */
        val distinctStyleableChildren = styleable.styleableChildren.distinctBy { it.styleableResId.resourceName }
        for (styleableChildInfo in distinctStyleableChildren) {

            val functionName = styleable.attrResourceNameToCamelCase(styleableChildInfo.styleableResId.resourceName)

            // Sub-styles can be resources: "view.style { titleStyle(R.style...) }"
            function(functionName) {
                receiver(extendableStyleBuilderTypeName)
                parameter("resId", Integer.TYPE) {
                    addAnnotation(STYLE_RES)
                }
                addStatement(
                    "builder.putStyle(%T.styleable.%L[%L], resId)",
                    styleableChildInfo.styleableResId.rClassName.toKPoet(),
                    styleable.styleableResourceName,
                    styleableChildInfo.styleableResId.kotlinCode
                )

                addOriginatingElement(styleable.annotatedElement)
                addOriginatingElement(styleableChildInfo.element)
            }

            // Sub-styles can be style objects: "view.style { titleStyle(styleObject) }"
            function(functionName) {
                receiver(extendableStyleBuilderTypeName)
                parameter("style", STYLE_CLASS_NAME.toKPoet())
                addStatement(
                    "builder.putStyle(%T.styleable.%L[%L], style)",
                    styleableChildInfo.styleableResId.rClassName.toKPoet(),
                    styleable.styleableResourceName,
                    styleableChildInfo.styleableResId.kotlinCode
                )
                addOriginatingElement(styleable.annotatedElement)
                addOriginatingElement(styleableChildInfo.element)
            }

            /*
             * Sub-styles can be built on the spot:
             * view.style {
             *     titleStyle {
             *         textSize(10)
             *     }
             * }
             */
            function(functionName) {
                receiver(extendableStyleBuilderTypeName)

                val subExtendableStyleBuilderTypeName = EXTENDABLE_STYLE_BUILDER_CLASS_NAME.toKPoet().parameterizedBy(
                    styleableChildInfo.type.typeNameKotlin()
                )
                val builderParameter = parameter(
                    "init", LambdaTypeName.get(
                        receiver = subExtendableStyleBuilderTypeName,
                        returnType = UNIT
                    )
                )
                addStatement(
                    "builder.putStyle(%T.styleable.%L[%L], %T().apply(%N).build())",
                    styleableChildInfo.styleableResId.rClassName.toKPoet(),
                    styleable.styleableResourceName,
                    styleableChildInfo.styleableResId.kotlinCode,
                    subExtendableStyleBuilderTypeName,
                    builderParameter
                )
                addOriginatingElement(styleable.annotatedElement)
                addOriginatingElement(styleableChildInfo.element)
            }
        }

        /*
         * Style builder extensions to set attributes.
         *
         * These are available to style builders of subclasses as well.
         *
         * Usage is like: "view.style { padding(10) }"
         */
        val attrGroups = styleable.attrs.groupBy { it.styleableResId.resourceName }
        for (groupedAttrs in attrGroups.values) {

            val nonResTargetAttrs = groupedAttrs.filter { it.targetFormat != Format.RESOURCE_ID }

            val isTargetDimensionType = nonResTargetAttrs.any { it.targetFormat.isDimensionType }
            val isTargetColorStateListType = nonResTargetAttrs.any { it.targetFormat.isColorStateListType }

            val attr = if (nonResTargetAttrs.isNotEmpty()) nonResTargetAttrs.first() else groupedAttrs.first()
            val attrResourceName = attr.styleableResId.resourceName
            val baseMethodName = styleable.attrResourceNameToCamelCase(attrResourceName)

            // If the target type isn't a resource: "view.style { padding(10) }"
            if (nonResTargetAttrs.isNotEmpty()) {
                function(baseMethodName) {
                    addKdoc(attr.kdoc)
                    receiver(extendableStyleBuilderTypeName)

                    addRequiresApiAnnotation(this, attr)

                    // TODO Make sure that this works correctly when the view code is in Kotlin and already using Kotlin types
                    parameter("value", attr.targetType.typeNameKotlin().copy(nullable = attr.targetFormat.isNullable)) {
                        // Filter out the Nullable annotation since we defer to idiomatic Kotlin by attaching
                        // the nullability to the type.
                        attr.targetFormat.valueAnnotation?.takeIf { it != AndroidClassNames.NULLABLE }?.let {
                            addAnnotation(it.toKPoet())
                        }
                    }

                    addStatement(
                        "builder.put(%T.styleable.%L[%L], value)",
                        attr.styleableResId.rClassName.toKPoet(),
                        styleable.styleableResourceName,
                        attr.styleableResId.kotlinCode
                    )

                    addOriginatingElement(styleable.annotatedElement)
                    addOriginatingElement(attr.element)
                }
            }

            // Each attribute can be set with a resource: "view.style { paddingRes(R.dimen...) }"
            function("${baseMethodName}Res") {
                addKdoc(attr.kdoc)
                receiver(extendableStyleBuilderTypeName)

                addRequiresApiAnnotation(this, attr)

                parameter("resId", Integer.TYPE) {
                    addAnnotation(attr.targetFormat.resAnnotation)
                }

                addStatement(
                    "builder.putRes(%T.styleable.%L[%L], resId)",
                    attr.styleableResId.rClassName.toKPoet(),
                    styleable.styleableResourceName,
                    attr.styleableResId.kotlinCode
                )

                addOriginatingElement(styleable.annotatedElement)
                addOriginatingElement(attr.element)
            }

            // Adds a special <attribute>Dp method that automatically converts a dp value to pixels for dimensions
            if (isTargetDimensionType) {
                function("${baseMethodName}Dp") {
                    addKdoc(attr.kdoc)
                    receiver(extendableStyleBuilderTypeName)

                    addRequiresApiAnnotation(this, attr)

                    parameter("value", Integer.TYPE) {
                        addAnnotation(
                            AnnotationSpec.builder(AndroidClassNames.DIMENSION.toKPoet())
                                .addMember("unit = %T.DP", AndroidClassNames.DIMENSION.toKPoet())
                                .build()
                        )
                    }

                    addStatement(
                        "builder.putDp(%T.styleable.%L[%L], value)",
                        attr.styleableResId.rClassName.toKPoet(),
                        styleable.styleableResourceName,
                        attr.styleableResId.kotlinCode
                    )

                    addOriginatingElement(styleable.annotatedElement)
                    addOriginatingElement(attr.element)
                }
            }

            // Adds a special <attribute> method that automatically converts a @ColorInt to a ColorStateList
            if (isTargetColorStateListType) {
                function(baseMethodName) {
                    addKdoc(attr.kdoc)
                    receiver(extendableStyleBuilderTypeName)

                    addRequiresApiAnnotation(this, attr)

                    parameter("color", Integer.TYPE) {
                        addAnnotation(COLOR_INT)
                    }

                    addStatement(
                        "builder.putColor(%T.styleable.%L[%L], color)",
                        attr.styleableResId.rClassName.toKPoet(),
                        styleable.styleableResourceName,
                        attr.styleableResId.kotlinCode
                    )

                    addOriginatingElement(styleable.annotatedElement)
                    addOriginatingElement(attr.element)
                }
            }
        }

        /*
         * A helper (not an extension) for creating a Style object for the given view.
         *
         * Usage is like: "val style = imageViewStyle {  // builder as a receiver here  }"
         */
        function("${styleable.viewElementName.decapitalize()}Style") {
            addModifiers(KModifier.INLINE)
            returns(STYLE_CLASS_NAME.toKPoet())

            val extendableStyleBuilderTypeName = EXTENDABLE_STYLE_BUILDER_CLASS_NAME.toKPoet().parameterizedBy(
                styleable.viewElementType.typeNameKotlin()
            )

            val builderParam = parameter(
                "builder",
                LambdaTypeName.get(
                    receiver = extendableStyleBuilderTypeName,
                    returnType = UNIT
                )
            )

            addStatement(
                "return %T().apply(%N).build()",
                extendableStyleBuilderTypeName,
                builderParam
            )

            addOriginatingElement(styleable.annotatedElement)
        }
    }

    private fun addRequiresApiAnnotation(builder: FunSpec.Builder, attr: AttrInfo) {
        if (attr.requiresApi > 1) {
            builder.addAnnotation(
                AnnotationSpec.builder(RequiresApi::class.java)
                    .addMember("%L", attr.requiresApi)
                    .build()
            )
        }
    }
}
