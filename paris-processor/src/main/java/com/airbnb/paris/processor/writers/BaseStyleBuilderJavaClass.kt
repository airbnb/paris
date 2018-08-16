package com.airbnb.paris.processor.writers

import com.airbnb.paris.processor.*
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.models.StyleableInfo
import com.squareup.javapoet.*

internal class BaseStyleBuilderJavaClass(
    override val processor: ParisProcessor,
    parentStyleApplierClassName: ClassName?,
    rClassName: ClassName?,
    styleablesTree: StyleablesTree,
    styleableInfo: StyleableInfo
) : SkyJavaClass(processor), WithSkyProcessor {

    override val packageName: String
    override val name: String

    init {
        val styleApplierClassName = styleableInfo.styleApplierClassName
        val className = ClassName.get(
            styleApplierClassName.packageName(),
            styleApplierClassName.simpleName(),
            "BaseStyleBuilder"
        )
        packageName = className.packageName()!!
        name = className.simpleName()!!
    }

    override val block: TypeSpec.Builder.() -> Unit = {
        val styleApplierClassName = styleableInfo.styleApplierClassName

        val superStyleBuilderClassName = if (parentStyleApplierClassName != null) {
            parentStyleApplierClassName.nestedClass("BaseStyleBuilder")
        } else {
            STYLE_BUILDER_CLASS_NAME
        }
        val wildcardTypeName = WildcardTypeName.subtypeOf(Object::class.java)
        val baseClassName = ClassName.get(
            styleApplierClassName.packageName(),
            styleApplierClassName.simpleName(),
            "BaseStyleBuilder"
        )

        addTypeVariable(
            TypeVariableName.get(
                "B",
                ParameterizedTypeName.get(
                    baseClassName,
                    TypeVariableName.get("B"),
                    TypeVariableName.get("A")
                )
            )
        )
        addTypeVariable(
            TypeVariableName.get(
                "A",
                ParameterizedTypeName.get(STYLE_APPLIER_CLASS_NAME, wildcardTypeName, wildcardTypeName)
            )
        )
        public()
        static()
        abstract()
        superclass(
            ParameterizedTypeName.get(
                superStyleBuilderClassName,
                TypeVariableName.get("B"),
                TypeVariableName.get("A")
            )
        )

        constructor {
            public()
            addParameter(TypeVariableName.get("A"), "applier")
            addStatement("super(applier)")
        }

        constructor {
            public()
        }

        val distinctStyleableChildren =
            styleableInfo.styleableChildren.distinctBy { it.styleableResId.resourceName }
        for (styleableChildInfo in distinctStyleableChildren) {
            rClassName!!

            val methodName =
                styleableInfo.attrResourceNameToCamelCase(styleableChildInfo.styleableResId.resourceName)

            method(methodName) {
                public()
                addParameter(
                    ParameterSpec.builder(Integer.TYPE, "resId")
                        .addAnnotation(AndroidClassNames.STYLE_RES)
                        .build()
                )
                returns(TypeVariableName.get("B"))
                addStatement(
                    "getBuilder().putStyle(\$T.styleable.\$L[\$L], resId)",
                    rClassName,
                    styleableInfo.styleableResourceName,
                    styleableChildInfo.styleableResId.code
                )
                addStatement("return (B) this")
            }

            method(methodName) {
                public()
                addParameter(ParameterSpec.builder(STYLE_CLASS_NAME, "style").build())
                returns(TypeVariableName.get("B"))
                addStatement(
                    "getBuilder().putStyle(\$T.styleable.\$L[\$L], style)",
                    rClassName,
                    styleableInfo.styleableResourceName,
                    styleableChildInfo.styleableResId.code
                )
                addStatement("return (B) this")
            }

            val subStyleApplierClassName = styleablesTree.findStyleApplier(
                styleableChildInfo.type.asTypeElement()
            )
            val subStyleBuilderClassName = subStyleApplierClassName.nestedClass("StyleBuilder")
            method(methodName) {
                public()
                addParameter(
                    ParameterSpec.builder(
                        ParameterizedTypeName.get(
                            STYLE_BUILDER_FUNCTION_CLASS_NAME,
                            subStyleBuilderClassName
                        ), "function"
                    ).build()
                )
                returns(TypeVariableName.get("B"))
                addStatement(
                    "\$T subBuilder = new \$T()",
                    subStyleBuilderClassName,
                    subStyleBuilderClassName
                )
                addStatement("function.invoke(subBuilder)")
                addStatement(
                    "getBuilder().putStyle(\$T.styleable.\$L[\$L], subBuilder.build())",
                    rClassName,
                    styleableInfo.styleableResourceName,
                    styleableChildInfo.styleableResId.code
                )
                addStatement("return (B) this")
            }
        }

        val groupedAttrInfos = styleableInfo.attrs.groupBy { it.styleableResId.resourceName }
        for (groupedAttrs in groupedAttrInfos.values) {
            rClassName!!

            val nonResTargetAttrs = groupedAttrs.filter { it.targetFormat != Format.RESOURCE_ID }

            if (nonResTargetAttrs.isNotEmpty() && nonResTargetAttrs.distinctBy { it.targetType }.size > 1) {
                logError {
                    "The same @Attr value can't be used on methods with different parameter types (excluding resource id types)"
                }
            }

            val isTargetDimensionType = nonResTargetAttrs.any { it.targetFormat.isDimensionType }
            val isTargetColorStateListType =
                nonResTargetAttrs.any { it.targetFormat.isColorStateListType }

            val attr =
                if (nonResTargetAttrs.isNotEmpty()) nonResTargetAttrs.first() else groupedAttrs.first()
            val attrResourceName = attr.styleableResId.resourceName
            val baseMethodName = styleableInfo.attrResourceNameToCamelCase(attrResourceName)

            if (nonResTargetAttrs.isNotEmpty()) {
                method(baseMethodName) {
                    addJavadoc(attr.javadoc)

                    val valueParameterBuilder =
                        ParameterSpec.builder(TypeName.get(attr.targetType), "value")
                    attr.targetFormat.valueAnnotation?.let {
                        valueParameterBuilder.addAnnotation(it)
                    }

                    public()
                    addParameter(valueParameterBuilder.build())
                    returns(TypeVariableName.get("B"))
                    addStatement(
                        "getBuilder().put(\$T.styleable.\$L[\$L], value)",
                        rClassName,
                        styleableInfo.styleableResourceName,
                        attr.styleableResId.code
                    )
                    addStatement("return (B) this")
                }
            }

            method("${baseMethodName}Res") {
                addJavadoc(attr.javadoc)
                public()
                addParameter(
                    ParameterSpec.builder(Integer.TYPE, "resId")
                        .addAnnotation(attr.targetFormat.resAnnotation)
                        .build()
                )
                returns(TypeVariableName.get("B"))
                addStatement(
                    "getBuilder().putRes(\$T.styleable.\$L[\$L], resId)",
                    rClassName,
                    styleableInfo.styleableResourceName,
                    attr.styleableResId.code
                )
                addStatement("return (B) this")
            }

            // Adds a special <attribute>Dp method that automatically converts a dp value to pixels for dimensions
            if (isTargetDimensionType) {
                method("${baseMethodName}Dp") {
                    addJavadoc(attr.javadoc)
                    public()
                    addParameter(
                        ParameterSpec.builder(Integer.TYPE, "value")
                            .addAnnotation(
                                AnnotationSpec.builder(AndroidClassNames.DIMENSION)
                                    .addMember("unit", "\$T.DP", AndroidClassNames.DIMENSION)
                                    .build()
                            )
                            .build()
                    )
                    returns(TypeVariableName.get("B"))
                    addStatement(
                        "getBuilder().putDp(\$T.styleable.\$L[\$L], value)",
                        rClassName,
                        styleableInfo.styleableResourceName,
                        attr.styleableResId.code
                    )
                    addStatement("return (B) this")
                }
            }

            // Adds a special <attribute> method that automatically converts a @ColorInt to a ColorStateList
            if (isTargetColorStateListType) {
                method(baseMethodName) {
                    addJavadoc(attr.javadoc)
                    public()
                    addParameter(
                        ParameterSpec.builder(Integer.TYPE, "color")
                            .addAnnotation(AndroidClassNames.COLOR_INT)
                            .build()
                    )
                    returns(TypeVariableName.get("B"))
                    addStatement(
                        "getBuilder().putColor(\$T.styleable.\$L[\$L], color)",
                        rClassName,
                        styleableInfo.styleableResourceName,
                        attr.styleableResId.code
                    )
                    addStatement("return (B) this")
                }
            }
        }

        method("applyTo") {
            public()
            addParameter(
                ParameterSpec.builder(
                    TypeName.get(styleableInfo.viewElementType),
                    "view"
                ).build()
            )
            returns(TypeVariableName.get("B"))
            addStatement("new \$T(view).apply(build())", styleApplierClassName)
            addStatement("return (B) this")
        }
    }
}
