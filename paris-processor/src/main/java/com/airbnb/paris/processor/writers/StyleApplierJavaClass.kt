package com.airbnb.paris.processor.writers

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.addOriginatingElement
import com.airbnb.paris.processor.Format
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_APPLIER_CLASS_NAME
import com.airbnb.paris.processor.STYLE_APPLIER_UTILS_CLASS_NAME
import com.airbnb.paris.processor.STYLE_CLASS_NAME
import com.airbnb.paris.processor.StyleablesTree
import com.airbnb.paris.processor.TYPED_ARRAY_WRAPPER_CLASS_NAME
import com.airbnb.paris.processor.framework.AndroidClassNames
import com.airbnb.paris.processor.framework.SkyJavaClass
import com.airbnb.paris.processor.framework.codeBlock
import com.airbnb.paris.processor.framework.constructor
import com.airbnb.paris.processor.framework.controlFlow
import com.airbnb.paris.processor.framework.final
import com.airbnb.paris.processor.framework.method
import com.airbnb.paris.processor.framework.override
import com.airbnb.paris.processor.framework.protected
import com.airbnb.paris.processor.framework.public
import com.airbnb.paris.processor.framework.static
import com.airbnb.paris.processor.models.EmptyStyleInfo
import com.airbnb.paris.processor.models.StyleResInfo
import com.airbnb.paris.processor.models.StyleStaticMethodInfo
import com.airbnb.paris.processor.models.StyleStaticPropertyInfo
import com.airbnb.paris.processor.models.StyleableInfo

import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec

internal class StyleApplierJavaClass(
    val parisProcessor: ParisProcessor,
    styleablesTree: StyleablesTree,
    styleableInfo: StyleableInfo
) : SkyJavaClass(parisProcessor) {

    override val packageName = styleableInfo.styleApplierClassName.packageName()!!
    override val name = styleableInfo.styleApplierClassName.simpleName()!!
    override val originatingElements: List<XElement> = listOfNotNull(
        styleableInfo.annotatedElement,
        // The R.style class is used to look up matching default style names, so if
        // the R class changes it can affect the code we generate.
        parisProcessor.memoizer.rStyleTypeElementX
    )

    override val block: TypeSpec.Builder.() -> Unit = {
        addAnnotation(AndroidClassNames.UI_THREAD)
        public()
        final()
        superclass(
            ParameterizedTypeName.get(
                STYLE_APPLIER_CLASS_NAME,
                styleableInfo.elementType.typeName,
                styleableInfo.viewElementType.typeName
            )
        )

        constructor {
            public()
            addParameter(styleableInfo.viewElementType.typeName, "view")
            if (styleableInfo.elementType == styleableInfo.viewElementType) {
                addStatement("super(view)")
            } else {
                // Different types means this style applier uses a proxy
                addStatement("super(new \$T(view))", styleableInfo.elementType.typeName)
            }
        }

        // If the view type is "View" then there is no parent
        var parentStyleApplierClassName: ClassName? = null
        if (!parisProcessor.memoizer.androidViewClassTypeX.isSameType(styleableInfo.viewElementType)) {
            val parentStyleApplierDetails = styleablesTree.findStyleApplier(
                styleableInfo.viewElementType.typeElement?.superType?.typeElement!!,
                errorContext = {"Parent view: ${styleableInfo.viewElementType.typeElement?.qualifiedName}"}
            )

            parentStyleApplierClassName = parentStyleApplierDetails.className
            addOriginatingElement(parentStyleApplierDetails.annotatedElement)

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

        if (styleableInfo.styleableResourceName.isNotEmpty()) {
            method("attributes") {
                override()
                protected()
                returns(ArrayTypeName.of(Integer.TYPE))
                addStatement("return \$T.styleable.\$L", styleableInfo.styleableRClassName ?: parisProcessor.RElement?.className, styleableInfo.styleableResourceName)
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
                    addStatement("getProxy().\$N(style)", beforeStyle.jvmName)
                }

                for (attr in styleableInfo.attrs) {
                    val applyAttribute: MethodSpec.Builder.() -> Unit = {
                        controlFlow("if (a.hasValue(\$L))", attr.styleableResId.code) {
                            addStatement(
                                "getProxy().\$N(\$L)",
                                attr.jvmName,
                                attr.targetFormat.typedArrayMethodCode("a", attr.styleableResId.code)
                            )
                        }

                        if (attr.defaultValueResId != null) {
                            controlFlow("else if (style.getShouldApplyDefaults())") {
                                addStatement(
                                    "getProxy().\$N(\$L)",
                                    attr.jvmName,
                                    attr.targetFormat.resourcesMethodCode(
                                        "context",
                                        "res",
                                        attr.defaultValueResId.code
                                    )
                                )
                            }
                        }

                    }

                    // If the attribute requires a minimum SDK version we add a conditional check before accessing the typed array.
                    if (attr.requiresApi > 1) {
                        controlFlow("if (\$T.VERSION.SDK_INT >= \$L)", arrayOf<Any>(AndroidClassNames.BUILD, attr.requiresApi)) {
                            applyAttribute()
                        }
                    } else {
                        applyAttribute()
                    }
                }

                // TODO Move to different method
                for (afterStyle in styleableInfo.afterStyles) {
                    addStatement("getProxy().\$N(style)", afterStyle.jvmName)
                }
            }
        }

        val styleApplierClassName = styleableInfo.styleApplierClassName

        addType(
            BaseStyleBuilderJavaClass(
                parisProcessor,
                parentStyleApplierClassName,
                styleablesTree,
                styleableInfo
            ).build()
        )
        val styleBuilderClassName = styleApplierClassName.nestedClass("StyleBuilder")
        addType(StyleBuilderJavaClass(parisProcessor, styleableInfo).build())

        // builder() method
        method("builder") {
            public()
            returns(styleBuilderClassName)
            addStatement("return new \$T(this)", styleBuilderClassName)
        }

        for (styleableChildInfo in styleableInfo.styleableChildren) {
            val (subStyleApplierAnnotatedElement, subStyleApplierClassName) = styleablesTree.findStyleApplier(
                styleableChildInfo.type.typeElement!!
            )
            // If the name of the proxy or subStyle type changes then our generated code needs to update as well,
            // therefore we must depend on it as an originating element.
            addOriginatingElement(subStyleApplierAnnotatedElement)

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
                    is StyleStaticPropertyInfo -> addStatement("apply(\$T.\$L)", styleInfo.enclosingElement.className, styleInfo.javaGetter)
                    is StyleStaticMethodInfo -> {
                        addStatement(
                            "\$T builder = new \$T()",
                            styleBuilderClassName,
                            styleBuilderClassName
                        )
                            .addStatement(
                                "\$T.\$L(builder)",
                                styleInfo.enclosingElement.className,
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
            addJavadoc("For debugging\n")
            public()
            static()
            addParameter(AndroidClassNames.CONTEXT, "context")

            if (styleableInfo.styles.size > 1) {
                addStatement(
                    "\$T \$T = new \$T(context)",
                    styleableInfo.viewElementType.typeName,
                    styleableInfo.viewElementType.typeName,
                    styleableInfo.viewElementType.typeName
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
                    styleableInfo.viewElementType.typeName,
                    styleVarargCode
                )
                addCode(assertEqualAttributesCode)
            }
        }
    }
}
