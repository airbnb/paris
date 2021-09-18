package com.airbnb.paris.processor.writers

import androidx.room.compiler.processing.XElement
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.framework.AndroidClassNames
import com.airbnb.paris.processor.framework.SkyJavaClass
import com.airbnb.paris.processor.framework.constructor
import com.airbnb.paris.processor.framework.final
import com.airbnb.paris.processor.framework.method
import com.airbnb.paris.processor.framework.public
import com.airbnb.paris.processor.framework.static
import com.airbnb.paris.processor.models.EmptyStyleInfo
import com.airbnb.paris.processor.models.StyleResInfo
import com.airbnb.paris.processor.models.StyleStaticMethodInfo
import com.airbnb.paris.processor.models.StyleStaticPropertyInfo
import com.airbnb.paris.processor.models.StyleableInfo
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec

internal fun getStyleBuilderClassName(styleApplierClassName: ClassName) =
    styleApplierClassName.nestedClass("StyleBuilder")

internal class StyleBuilderJavaClass(
    processor: ParisProcessor,
    styleableInfo: StyleableInfo
) : SkyJavaClass(processor) {

    override val packageName: String
    override val name: String
    override val originatingElements: List<XElement> = listOf(styleableInfo.annotatedElement)

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
                    is StyleStaticPropertyInfo -> addStatement("add(\$T.\$L)", it.enclosingElement.className, it.javaGetter)
                    is StyleStaticMethodInfo -> {
                        addStatement("consumeProgrammaticStyleBuilder()")
                        addStatement("debugName(\$S)", it.formattedName)
                        addStatement("\$T.\$L(this)", it.enclosingElement.className, it.elementName)
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
