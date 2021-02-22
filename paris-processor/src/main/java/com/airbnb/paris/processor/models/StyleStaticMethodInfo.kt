package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.Style
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.abstractions.XExecutableElement
import com.airbnb.paris.processor.framework.JavaCodeBlock
import com.airbnb.paris.processor.framework.KotlinCodeBlock
import com.airbnb.paris.processor.framework.isNotStatic
import com.airbnb.paris.processor.framework.isPrivate
import com.airbnb.paris.processor.framework.isProtected
import com.airbnb.paris.processor.framework.models.SkyStaticMethodModel
import com.airbnb.paris.processor.framework.models.SkyStaticMethodModelFactory
import com.airbnb.paris.processor.framework.toKPoet
import com.airbnb.paris.processor.utils.ParisProcessorUtils
import javax.lang.model.element.ExecutableElement

internal class StyleStaticMethodInfoExtractor(processor: ParisProcessor) :
    SkyStaticMethodModelFactory<StyleStaticMethodInfo>(processor, Style::class.java) {

    override fun elementToModel(element: XExecutableElement): StyleStaticMethodInfo? {
        // TODO Get Javadoc from field/method and add it to the generated methods

        if (!element.isStatic() || element.isPrivate() || element.isProtected()) {
            logError(element) {
                "Methods annotated with @Style must be static and can't be private or protected."
            }
            return null
        }

        val style = element.toAnnotationBox(Style::class)
        val isDefault = style!!.value.isDefault

        val enclosingElement = element.enclosingTypeElement

        val elementName = element.name

        val formattedName = ParisProcessorUtils.reformatStyleFieldOrMethodName(elementName)

        // TODO Check that the target type is a builder
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
    element: XExecutableElement,
    override val elementName: String,
    override val formattedName: String,
    override val javadoc: JavaCodeBlock,
    override val kdoc: KotlinCodeBlock,
    override val isDefault: Boolean = false
) : SkyStaticMethodModel(element), StyleInfo
