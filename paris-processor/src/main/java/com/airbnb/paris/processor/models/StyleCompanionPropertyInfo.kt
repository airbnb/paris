package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.Style
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.framework.models.SkyCompanionPropertyModel
import com.airbnb.paris.processor.framework.models.SkyCompanionPropertyModelFactory
import com.airbnb.paris.processor.utils.ParisProcessorUtils
import javax.lang.model.element.VariableElement

internal class StyleCompanionPropertyInfoExtractor
    : SkyCompanionPropertyModelFactory<StyleCompanionPropertyInfo>(Style::class.java) {

    override fun elementToModel(element: VariableElement): StyleCompanionPropertyInfo? {
        // TODO Get Javadoc from field/method and add it to the generated methods
        // TODO Check that the target type is an int or a Style

        if (element.isNotStatic()) {
            logError(element) {
                "Fields annotated with @Style must be static."
            }
            return null
        }

        if (element.isNotFinal()) {
            logError(element) {
                "Fields annotated with @Style must be final."
            }
            return null
        }

        val style = element.getAnnotation(Style::class.java)
        val isDefault = style.isDefault

        val enclosingElement = element.enclosingElement

        val elementName = element.simpleName.toString()

        val formattedName = ParisProcessorUtils.reformatStyleFieldOrMethodName(elementName)

        val javadoc = JavaCodeBlock.of("@see \$T#\$N", enclosingElement, elementName)
        val kdoc = KotlinCodeBlock.of("@see %T.%N", enclosingElement, elementName)

        val styleInfo = StyleCompanionPropertyInfo(
            element,
            elementName,
            formattedName,
            javadoc,
            kdoc,
            isDefault
        )

        if (styleInfo.getterElement.isPrivate() || styleInfo.getterElement.isProtected()) {
            logError(element) {
                "Fields annotated with @Style can't be private or protected."
            }
            return null
        }

        return styleInfo
    }
}

internal class StyleCompanionPropertyInfo(
    element: VariableElement,
    override val elementName: String,
    override val formattedName: String,
    override val javadoc: JavaCodeBlock,
    override val kdoc: KotlinCodeBlock,
    override val isDefault: Boolean = false
) : SkyCompanionPropertyModel(element), StyleInfo
