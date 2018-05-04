package com.airbnb.paris.processor.writers

import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.models.*
import com.squareup.javapoet.*

internal fun getStyleBuilderClassName(styleApplierClassName: ClassName) =
    styleApplierClassName.nestedClass("StyleBuilder")

internal class StyleBuilderJavaClass(styleableInfo: StyleableInfo)
    : SkyJavaClass(block = {

    val styleApplierClassName = styleableInfo.styleApplierClassName

    val baseClassName = ClassName.get(styleApplierClassName.packageName(), styleApplierClassName.simpleName(), "BaseStyleBuilder")

    val styleBuilderClassName = getStyleBuilderClassName(styleApplierClassName)
    addAnnotation(AndroidClassNames.UI_THREAD)
    public()
    static()
    final()
    superclass(ParameterizedTypeName.get(baseClassName, styleBuilderClassName, styleApplierClassName))

    constructor {
        public()
        addParameter(styleApplierClassName, "applier")
        addStatement("super(applier)")
    }

    constructor {
        public()
    }

    styleableInfo.styles.forEach {
        method("add${it.formattedName}") {
            addJavadoc(it.javadoc)
            public()
            returns(styleBuilderClassName)

            when (it.elementKind) {
                StyleInfo.Kind.FIELD -> addStatement("add(\$T.\$L)", it.enclosingElement, it.elementName)
                StyleInfo.Kind.METHOD -> {
                    addStatement("consumeProgrammaticStyleBuilder()")
                    addStatement("debugName(\$S)", it.formattedName)
                    addStatement("\$T.\$L(this)", it.enclosingElement, it.elementName)
                    addStatement("consumeProgrammaticStyleBuilder()")
                }
                StyleInfo.Kind.STYLE_RES -> {
                    addStatement("add(\$L)", it.styleResourceCode)
                }
                StyleInfo.Kind.EMPTY -> {
                    // Do nothing!
                }
            }
            addStatement("return this")
        }
    }

}) {
    init {
        val className = getStyleBuilderClassName(styleableInfo.styleApplierClassName)
        packageName = className.packageName()
        name = className.simpleName()
    }
}
