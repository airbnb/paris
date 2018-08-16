package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.Style
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_CLASS_NAME
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.framework.models.SkyCompanionPropertyModel
import com.airbnb.paris.processor.framework.models.SkyCompanionPropertyModelFactory
import com.airbnb.paris.processor.utils.ParisProcessorUtils
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeKind

internal class StyleCompanionPropertyInfoExtractor(processor: ParisProcessor)
    : SkyCompanionPropertyModelFactory<StyleCompanionPropertyInfo>(processor, Style::class.java) {

    override fun elementToModel(element: VariableElement): StyleCompanionPropertyInfo? {
        // TODO Get Javadoc from field/method and add it to the generated methods

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

        val type = element.asType()
        if (!isSubtype(type, STYLE_CLASS_NAME.toTypeMirror()) && type.kind != TypeKind.INT && !type.isNonExistent()) {
            // Note: if the type is non existent we ignore this error check so that users don't need to change their kapt configuration, they'll still
            // get a build error though not as explicit.
            logError(element) {
                "Fields annotated with @Style must implement com.airbnb.paris.styles.Style or be of type int (and refer to a style resource)."
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
