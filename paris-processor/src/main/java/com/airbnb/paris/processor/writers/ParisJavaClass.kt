package com.airbnb.paris.processor.writers

import com.airbnb.paris.processor.*
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.models.*
import com.squareup.javapoet.*

internal class ParisJavaClass(parisClassPackageName: String, styleableClassesInfo: List<StyleableInfo>, externalStyleableClassesInfo: List<BaseStyleableInfo>)
    : SkyJavaClass(parisClassPackageName, PARIS_SIMPLE_CLASS_NAME, {

    public()
    final()

    val sortedStyleableClassesInfo = (styleableClassesInfo + externalStyleableClassesInfo).sortedBy {
        it.elementName
    }
    for (styleableClassInfo in sortedStyleableClassesInfo) {
        val styleApplierClassName = styleableClassInfo.styleApplierClassName()
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
            addStatement("\$T.assertStylesContainSameAttributes(context)", styleableClassInfo.styleApplierClassName())
        }
    }
})

internal fun getStyleBuilderClassName(styleableClassInfo: BaseStyleableInfo): ClassName =
        styleableClassInfo.styleApplierClassName().nestedClass("StyleBuilder")
