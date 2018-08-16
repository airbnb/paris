package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.Style
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.framework.models.SkyStaticMethodModel
import com.airbnb.paris.processor.framework.models.SkyStaticMethodModelFactory
import com.airbnb.paris.processor.utils.ParisProcessorUtils
import javax.lang.model.element.ExecutableElement

internal class StyleStaticMethodInfoExtractor(processor: ParisProcessor)
    : SkyStaticMethodModelFactory<StyleStaticMethodInfo>(processor, Style::class.java) {

    override fun elementToModel(element: ExecutableElement): StyleStaticMethodInfo? {
        // TODO Get Javadoc from field/method and add it to the generated methods

        if (element.isNotStatic() || element.isPrivate() || element.isProtected()) {
            logError(element) {
                "Methods annotated with @Style must be static and can't be private or protected."
            }
            return null
        }

        val style = element.getAnnotation(Style::class.java)
        val isDefault = style.isDefault

        val enclosingElement = element.enclosingElement

        val elementName = element.simpleName.toString()

        val formattedName = ParisProcessorUtils.reformatStyleFieldOrMethodName(elementName)

        // TODO Check that the target type is a builder
        val targetType = element.parameters[0].asType()

        val javadoc = JavaCodeBlock.of("@see \$T#\$N(\$T)", enclosingElement, elementName, targetType)
        val kdoc = KotlinCodeBlock.of("@see %T.%N", enclosingElement, elementName)

        return StyleStaticMethodInfo(
            element,
            elementName,
            formattedName,
            javadoc,
            kdoc,
            isDefault
        )
    }
}

internal class StyleStaticMethodInfo(
    element: ExecutableElement,
    override val elementName: String,
    override val formattedName: String,
    override val javadoc: JavaCodeBlock,
    override val kdoc: KotlinCodeBlock,
    override val isDefault: Boolean = false
) : SkyStaticMethodModel(element), StyleInfo
