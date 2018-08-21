package com.airbnb.paris.processor.writers

import com.airbnb.paris.processor.*
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.models.*
import com.squareup.javapoet.*

internal class StyleApplierJavaClass(
    override val processor: ParisProcessor,
    styleablesTree: StyleablesTree,
    styleableInfo: StyleableInfo
) : SkyJavaClass(processor), WithParisProcessor {

    override val packageName = styleableInfo.styleApplierClassName.packageName()!!
    override val name = styleableInfo.styleApplierClassName.simpleName()!!

    override val block: TypeSpec.Builder.() -> Unit = {
        addAnnotation(AndroidClassNames.UI_THREAD)
        public()
        final()
        superclass(
            ParameterizedTypeName.get(
                STYLE_APPLIER_CLASS_NAME,
                TypeName.get(styleableInfo.elementType),
                TypeName.get(styleableInfo.viewElementType)
            )
        )

        constructor {
            public()
            addParameter(TypeName.get(styleableInfo.viewElementType), "view")
            if (styleableInfo.elementType == styleableInfo.viewElementType) {
                addStatement("super(view)")
            } else {
                // Different types means this style applier uses a proxy
                addStatement("super(new \$T(view))", styleableInfo.elementType)
            }
        }

        // If the view type is "View" then there is no parent
        var parentStyleApplierClassName: ClassName? = null
        if (!isSameType(AndroidClassNames.VIEW.toTypeMirror(), styleableInfo.viewElementType)) {
            parentStyleApplierClassName = styleablesTree.findStyleApplier(
                styleableInfo.viewElementType.asTypeElement().superclass.asTypeElement()
            )
            method("applyParent") {
                override()
                protected()
                addParameter(STYLE_CLASS_NAME, "style")
                addStatement(
                    "\$T applier = new \$T(getView())",
                    parentStyleApplierClassName,
                    parentStyleApplierClassName
                )
                addStatement("applier.setDebugListener(getDebugListener())")
                addStatement("applier.apply(style)")
            }
        }

        if (!styleableInfo.styleableResourceName.isEmpty()) {
            method("attributes") {
                override()
                protected()
                returns(ArrayTypeName.of(Integer.TYPE))
                addStatement("return \$T.styleable.\$L", RElement, styleableInfo.styleableResourceName)
            }

            val attrsWithDefaultValue = styleableInfo.attrs
                .filter { it.defaultValueResId != null }
                .map { it.styleableResId }
                .toSet()
            if (attrsWithDefaultValue.isNotEmpty()) {
                method("attributesWithDefaultValue") {
                    override()
                    public()
                    returns(ArrayTypeName.of(Integer.TYPE))
                    addCode("return new int[] {")
                    for (attr in attrsWithDefaultValue) {
                        addCode("\$L,", attr.code)
                    }
                    addCode("};\n")
                }
            }

            method("processStyleableFields") {
                override()
                protected()
                addParameter(STYLE_CLASS_NAME, "style")
                addParameter(TYPED_ARRAY_WRAPPER_CLASS_NAME, "a")
                addStatement(
                    "\$T context = getView().getContext()",
                    AndroidClassNames.CONTEXT
                )
                addStatement(
                    "\$T res = context.getResources()",
                    AndroidClassNames.RESOURCES
                )

                for (styleableChild in styleableInfo.styleableChildren) {
                    controlFlow("if (a.hasValue(\$L))", styleableChild.styleableResId.code) {
                        addStatement(
                            "\$N().apply(\$L)",
                            styleableChild.name,
                            Format.STYLE.typedArrayMethodCode("a", styleableChild.styleableResId.code)
                        )
                    }

                    if (styleableChild.defaultValueResId != null) {
                        controlFlow("else") {
                            addStatement(
                                "\$N().apply(\$L)",
                                styleableChild.name,
                                Format.STYLE.resourcesMethodCode(
                                    "context",
                                    "res",
                                    styleableChild.defaultValueResId.code
                                )
                            )
                        }
                    }
                }
            }

            method("processAttributes") {
                override()
                protected()
                addParameter(STYLE_CLASS_NAME, "style")
                addParameter(TYPED_ARRAY_WRAPPER_CLASS_NAME, "a")
                addStatement(
                    "\$T context = getView().getContext()",
                    AndroidClassNames.CONTEXT
                )
                addStatement(
                    "\$T res = context.getResources()",
                    AndroidClassNames.RESOURCES
                )

                // TODO Move to different method
                for (beforeStyle in styleableInfo.beforeStyles) {
                    addStatement("getProxy().\$N(style)", beforeStyle.name)
                }

                for (attr in styleableInfo.attrs) {
                    controlFlow("if (a.hasValue(\$L))", attr.styleableResId.code) {
                        addStatement(
                            "getProxy().\$N(\$L)",
                            attr.name,
                            attr.targetFormat.typedArrayMethodCode("a", attr.styleableResId.code)
                        )
                    }

                    if (attr.defaultValueResId != null) {
                        controlFlow("else if (style.getShouldApplyDefaults())") {
                            addStatement(
                                "getProxy().\$N(\$L)",
                                attr.name,
                                attr.targetFormat.resourcesMethodCode(
                                    "context",
                                    "res",
                                    attr.defaultValueResId.code
                                )
                            )
                        }
                    }
                }

                // TODO Move to different method
                for (afterStyle in styleableInfo.afterStyles) {
                    addStatement("getProxy().\$N(style)", afterStyle.name)
                }
            }
        }

        val styleApplierClassName = styleableInfo.styleApplierClassName

        addType(
            BaseStyleBuilderJavaClass(
                processor,
                parentStyleApplierClassName,
                RElement?.className,
                styleablesTree,
                styleableInfo
            ).build()
        )
        val styleBuilderClassName = styleApplierClassName.nestedClass("StyleBuilder")
        addType(StyleBuilderJavaClass(processor, styleableInfo).build())

        // builder() method
        method("builder") {
            public()
            returns(styleBuilderClassName)
            addStatement("return new \$T(this)", styleBuilderClassName)
        }

        for (styleableChildInfo in styleableInfo.styleableChildren) {
            val subStyleApplierClassName = styleablesTree.findStyleApplier(
                styleableChildInfo.type.asTypeElement()
            )
            method(styleableChildInfo.name) {
                public()
                returns(subStyleApplierClassName)
                addStatement(
                    "\$T subApplier = new \$T(getProxy().\$L)",
                    subStyleApplierClassName,
                    subStyleApplierClassName,
                    styleableChildInfo.getter
                )
                addStatement("subApplier.setDebugListener(getDebugListener())")
                addStatement("return subApplier", subStyleApplierClassName, styleableChildInfo.name)
            }
        }

        for (styleInfo in styleableInfo.styles) {
            method("apply${styleInfo.formattedName}") {
                addJavadoc(styleInfo.javadoc)
                public()

                when (styleInfo) {
                    is StyleCompanionPropertyInfo -> addStatement("apply(\$T.\$L)", styleInfo.enclosingElement, styleInfo.javaGetter)
                    is StyleStaticMethodInfo -> {
                        addStatement(
                            "\$T builder = new \$T()",
                            styleBuilderClassName,
                            styleBuilderClassName
                        )
                            .addStatement(
                                "\$T.\$L(builder)",
                                styleInfo.enclosingElement,
                                styleInfo.elementName
                            )
                            .addStatement("apply(builder.build())")
                    }
                    is StyleResInfo -> addStatement("apply(\$L)", styleInfo.styleResourceCode)
                    is EmptyStyleInfo -> {
                        // Do nothing!
                    }
                }
            }
        }

        method("assertStylesContainSameAttributes") {
            addJavadoc("For debugging")
            public()
            static()
            addParameter(AndroidClassNames.CONTEXT, "context")

            if (styleableInfo.styles.size > 1) {
                addStatement(
                    "\$T \$T = new \$T(context)",
                    styleableInfo.viewElementType,
                    styleableInfo.viewElementType,
                    styleableInfo.viewElementType
                )

                val styleVarargCode = codeBlock {
                    for ((i, style) in styleableInfo.styles.withIndex()) {
                        if (i > 0) {
                            add(", ")
                        }
                        add(
                            "new \$T().add\$L().build()",
                            styleableInfo.styleBuilderClassName, style.formattedName
                        )
                    }
                }

                val assertEqualAttributesCode = CodeBlock.of(
                    "\$T.Companion.assertSameAttributes(new \$T(\$T), \$L);\n",
                    STYLE_APPLIER_UTILS_CLASS_NAME,
                    styleApplierClassName,
                    styleableInfo.viewElementType,
                    styleVarargCode
                )
                addCode(assertEqualAttributesCode)
            }
        }
    }
}
