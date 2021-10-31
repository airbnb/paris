package com.airbnb.paris.processor.writers

import androidx.annotation.RequiresApi
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.addOriginatingElement
import com.airbnb.paris.processor.Format
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_APPLIER_CLASS_NAME
import com.airbnb.paris.processor.STYLE_BUILDER_CLASS_NAME
import com.airbnb.paris.processor.STYLE_BUILDER_FUNCTION_CLASS_NAME
import com.airbnb.paris.processor.STYLE_CLASS_NAME
import com.airbnb.paris.processor.StyleablesTree
import com.airbnb.paris.processor.framework.AndroidClassNames
import com.airbnb.paris.processor.framework.SkyJavaClass
import com.airbnb.paris.processor.framework.abstract
import com.airbnb.paris.processor.framework.constructor
import com.airbnb.paris.processor.framework.method
import com.airbnb.paris.processor.framework.public
import com.airbnb.paris.processor.framework.static
import com.airbnb.paris.processor.models.AttrInfo
import com.airbnb.paris.processor.models.StyleableInfo
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import com.squareup.javapoet.TypeVariableName
import com.squareup.javapoet.WildcardTypeName

internal class BaseStyleBuilderJavaClass(
    val parisProcessor: ParisProcessor,
    parentStyleApplierClassName: ClassName?,
    styleablesTree: StyleablesTree,
    styleableInfo: StyleableInfo
) : SkyJavaClass(parisProcessor) {

    override val packageName: String
    override val name: String
    override val originatingElements: List<XElement> = listOfNotNull(
        styleableInfo.annotatedElement,
        parisProcessor.memoizer.rStyleTypeElementX
    )

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

            val methodName = styleableInfo.attrResourceNameToCamelCase(styleableChildInfo.styleableResId.resourceName)

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
                    styleableChildInfo.styleableResId.rClassName,
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
                    styleableChildInfo.styleableResId.rClassName,
                    styleableInfo.styleableResourceName,
                    styleableChildInfo.styleableResId.code
                )
                addStatement("return (B) this")
            }

            val (subStyleApplierAnnotatedElement, subStyleApplierClassName) = styleablesTree.findStyleApplier(
                styleableChildInfo.type.typeElement ?: error("${styleableChildInfo.type} does not have type element")
            )
            addOriginatingElement(subStyleApplierAnnotatedElement)

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
                    styleableChildInfo.styleableResId.rClassName,
                    styleableInfo.styleableResourceName,
                    styleableChildInfo.styleableResId.code
                )
                addStatement("return (B) this")
            }
        }

        val groupedAttrInfos = styleableInfo.attrs.groupBy { it.styleableResId.resourceName }
        for (groupedAttrs in groupedAttrInfos.values) {

            val nonResTargetAttrs = groupedAttrs.filter { it.targetFormat != Format.RESOURCE_ID }

            if (nonResTargetAttrs.isNotEmpty() && nonResTargetAttrs.distinctBy { it.targetType }.size > 1) {
                parisProcessor.logError {
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
                        ParameterSpec.builder(attr.targetType.typeName, "value")
                    attr.targetFormat.valueAnnotation?.let {
                        valueParameterBuilder.addAnnotation(it)
                    }

                    addRequiresApiAnnotation(this, attr)

                    public()
                    addParameter(valueParameterBuilder.build())
                    returns(TypeVariableName.get("B"))
                    addStatement(
                        "getBuilder().put(\$T.styleable.\$L[\$L], value)",
                        attr.styleableResId.rClassName,
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
                addRequiresApiAnnotation(this, attr)
                returns(TypeVariableName.get("B"))
                addStatement(
                    "getBuilder().putRes(\$T.styleable.\$L[\$L], resId)",
                    attr.styleableResId.rClassName,
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
                    addRequiresApiAnnotation(this, attr)
                    returns(TypeVariableName.get("B"))
                    addStatement(
                        "getBuilder().putDp(\$T.styleable.\$L[\$L], value)",
                        attr.styleableResId.rClassName,
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
                    addRequiresApiAnnotation(this, attr)
                    returns(TypeVariableName.get("B"))
                    addStatement(
                        "getBuilder().putColor(\$T.styleable.\$L[\$L], color)",
                        attr.styleableResId.rClassName,
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
                    styleableInfo.viewElementType.typeName,
                    "view"
                ).build()
            )
            returns(TypeVariableName.get("B"))
            addStatement("new \$T(view).apply(build())", styleApplierClassName)
            addStatement("return (B) this")
        }
    }

    private fun addRequiresApiAnnotation(builder: MethodSpec.Builder, attr: AttrInfo) {
        if (attr.requiresApi > 1) {
            builder.addAnnotation(
                AnnotationSpec.builder(RequiresApi::class.java)
                    .addMember("value", "\$L", attr.requiresApi)
                    .build()
            )
        }
    }
}
