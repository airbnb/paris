package com.airbnb.paris.processor.models

import androidx.room.compiler.processing.XMethodElement
import com.airbnb.paris.annotations.Style
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.framework.JavaCodeBlock
import com.airbnb.paris.processor.framework.KotlinCodeBlock
import com.airbnb.paris.processor.framework.models.SkyStaticMethodModel
import com.airbnb.paris.processor.framework.models.SkyStaticMethodModelFactory
import com.airbnb.paris.processor.framework.toKPoet
import com.airbnb.paris.processor.utils.ParisProcessorUtils

internal class StyleStaticMethodInfoExtractor(val parisProcessor: ParisProcessor) :
    SkyStaticMethodModelFactory<StyleStaticMethodInfo>(parisProcessor, Style::class.java) {

    override fun elementToModel(element: XMethodElement): StyleStaticMethodInfo? {
        // TODO Get Javadoc from field/method and add it to the generated methods

        if (!element.isStatic() || element.isPrivate() || element.isProtected()) {
            parisProcessor.logError(element) {
                "Methods annotated with @Style must be static and can't be private or protected."
            }
            return null
        }

        val style = element.getAnnotation(Style::class)
        val isDefault = style!!.value.isDefault

        val enclosingElement = element.enclosingElement

        val elementName = element.name

        val formattedName = ParisProcessorUtils.reformatStyleFieldOrMethodName(elementName)

        val targetType = element.parameters[0].type.typeName

        val javadoc = JavaCodeBlock.of("@see \$T#\$N(\$T)\n", enclosingElement.className, elementName, targetType)
        val kdoc = KotlinCodeBlock.of("@see %T.%N\n", enclosingElement.className.toKPoet(), elementName)

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
    element: XMethodElement,
    override val elementName: String,
    override val formattedName: String,
    override val javadoc: JavaCodeBlock,
    override val kdoc: KotlinCodeBlock,
    override val isDefault: Boolean = false
) : SkyStaticMethodModel(element), StyleInfo
