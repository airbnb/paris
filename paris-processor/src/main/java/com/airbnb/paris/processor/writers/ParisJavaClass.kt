package com.airbnb.paris.processor.writers

import com.airbnb.paris.processor.*
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.models.*
import com.squareup.javapoet.*
import java.util.*

internal class ParisJavaFile(processor: ParisProcessor, parisClassPackageName: String, styleableClassesInfo: List<StyleableInfo>, externalStyleableClassesInfo: List<BaseStyleableInfo>)
    : SkyJavaClass<ParisProcessor>(processor, parisClassPackageName, PARIS_SIMPLE_CLASS_NAME, {

    public()
    final()

    val sortedStyleableClassesInfo = (styleableClassesInfo + externalStyleableClassesInfo).sortedBy {
        it.elementName
    }
    for (styleableClassInfo in sortedStyleableClassesInfo) {
        val styleApplierClassName = getStyleApplierClassName(styleableClassInfo)
        val viewParameterTypeName = TypeName.get(styleableClassInfo.viewElementType)

        method("style") {
            public()
            static()
            returns(styleApplierClassName)
            addParameter(viewParameterTypeName, "view")
            addStatement("return new \$T(view)", styleApplierClassName)
        }

        val styleBuilderClassName = styleApplierClassName.nestedClass("StyleBuilder")

        method("styleBuilder") {
            public()
            static()
            returns(styleBuilderClassName)
            addParameter(viewParameterTypeName, "view")
            addStatement("return new \$T(new \$T(view))", styleBuilderClassName, styleApplierClassName)
        }
    }

    method("assertStylesContainSameAttributes") {
        addJavadoc("For debugging")
        public()
        static()
        addParameter(AndroidClassNames.CONTEXT, "context")

        for (styleableClassInfo in styleableClassesInfo) {
            if (styleableClassInfo.styles.size > 1) {
                addStatement("\$T \$T = new \$T(context)", styleableClassInfo.elementType, styleableClassInfo.elementType, styleableClassInfo.elementType)

                val styleVarargCode = codeBlock {
                    for ((i, style) in styleableClassInfo.styles.withIndex()) {
                        if (i > 0) {
                            add(", ")
                        }
                        add("new \$T().add\$L().build()",
                                getStyleBuilderClassName(styleableClassInfo), style.formattedName)
                    }
                }

                val assertEqualAttributesCode = CodeBlock.of(
                        "\$T.Companion.assertSameAttributes(style(\$T), \$L);\n",
                        STYLE_APPLIER_UTILS_CLASS_NAME,
                        styleableClassInfo.elementType,
                        styleVarargCode)
                addCode(assertEqualAttributesCode)
            }
        }
    }
})

internal fun getStyleApplierClassName(styleableClassInfo: BaseStyleableInfo): ClassName {
    return ClassName.get(
            styleableClassInfo.elementPackageName,
            String.format(Locale.US, STYLE_APPLIER_SIMPLE_CLASS_NAME_FORMAT, styleableClassInfo.elementName)
    )
}

internal fun getStyleBuilderClassName(styleableClassInfo: BaseStyleableInfo): ClassName =
        getStyleApplierClassName(styleableClassInfo).nestedClass("StyleBuilder")
