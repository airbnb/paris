package com.airbnb.paris.processor.writers

import com.airbnb.paris.processor.PARIS_SIMPLE_CLASS_NAME
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.SPANNABLE_BUILDER_CLASS_NAME
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.models.BaseStyleableInfo
import com.airbnb.paris.processor.models.StyleableInfo
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec

internal class ParisJavaClass(
    override val processor: ParisProcessor,
    parisClassPackageName: String,
    styleableClassesInfo: List<StyleableInfo>,
    externalStyleableClassesInfo: List<BaseStyleableInfo>
) : SkyJavaClass(processor) {

    override val packageName: String = parisClassPackageName
    override val name: String = PARIS_SIMPLE_CLASS_NAME

    override val block: TypeSpec.Builder.() -> Unit = {
        public()
        final()

        val sortedStyleableClassesInfo =
            (styleableClassesInfo + externalStyleableClassesInfo).sortedBy {
                it.elementName
            }
        for (styleableClassInfo in sortedStyleableClassesInfo) {
            val styleApplierClassName = styleableClassInfo.styleApplierClassName
            val viewParameterTypeName = TypeName.get(styleableClassInfo.viewElementType)

            method("style") {
                public()
                static()
                returns(styleApplierClassName)
                addParameter(viewParameterTypeName, "view")
                addStatement("return new \$T(view)", styleApplierClassName)
            }

            val styleBuilderClassName = styleableClassInfo.styleBuilderClassName

            method("styleBuilder") {
                public()
                static()
                returns(styleBuilderClassName)
                addParameter(viewParameterTypeName, "view")
                addStatement(
                    "return new \$T(new \$T(view))",
                    styleBuilderClassName,
                    styleApplierClassName
                )
            }
        }

        method("spannableBuilder") {
            public()
            static()
            returns(SPANNABLE_BUILDER_CLASS_NAME)

            addStatement("return new \$T()", SPANNABLE_BUILDER_CLASS_NAME)
        }

        // TODO Should the method take in an Activity since anything else seems to screw up view inflation?
        method("assertStylesContainSameAttributes") {
            addJavadoc("For debugging")
            public()
            static()
            addParameter(AndroidClassNames.CONTEXT, "context")

            for (styleableClassInfo in sortedStyleableClassesInfo) {
                addStatement(
                    "\$T.assertStylesContainSameAttributes(context)",
                    styleableClassInfo.styleApplierClassName
                )
            }
        }
    }
}
