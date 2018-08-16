package com.airbnb.paris.processor.writers

import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.models.*
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec

internal fun getStyleBuilderClassName(styleApplierClassName: ClassName) =
    styleApplierClassName.nestedClass("StyleBuilder")

internal class StyleBuilderJavaClass(
    override val processor: ParisProcessor,
    styleableInfo: StyleableInfo
) : SkyJavaClass(processor) {

    override val packageName: String
    override val name: String

    init {
        val className = getStyleBuilderClassName(styleableInfo.styleApplierClassName)
        packageName = className.packageName()
        name = className.simpleName()
    }

    override val block: TypeSpec.Builder.() -> Unit = {
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

                when (it) {
                    is StyleCompanionPropertyInfo -> addStatement("add(\$T.\$L)", it.enclosingElement, it.javaGetter)
                    is StyleStaticMethodInfo -> {
                        addStatement("consumeProgrammaticStyleBuilder()")
                        addStatement("debugName(\$S)", it.formattedName)
                        addStatement("\$T.\$L(this)", it.enclosingElement, it.elementName)
                        addStatement("consumeProgrammaticStyleBuilder()")
                    }
                    is StyleResInfo -> addStatement("add(\$L)", it.styleResourceCode)
                    is EmptyStyleInfo -> {
                        // Do nothing!
                    }
                }
                addStatement("return this")
            }
        }
    }
}
