package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.Style
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_CLASS_NAME
import com.airbnb.paris.processor.abstractions.XFieldElement
import com.airbnb.paris.processor.abstractions.isInt
import com.airbnb.paris.processor.framework.JavaCodeBlock
import com.airbnb.paris.processor.framework.KotlinCodeBlock
import com.airbnb.paris.processor.framework.isNotFinal
import com.airbnb.paris.processor.framework.isNotStatic
import com.airbnb.paris.processor.framework.isPrivate
import com.airbnb.paris.processor.framework.isProtected
import com.airbnb.paris.processor.framework.models.SkyCompanionPropertyModel
import com.airbnb.paris.processor.framework.models.SkyCompanionPropertyModelFactory
import com.airbnb.paris.processor.framework.toKPoet
import com.airbnb.paris.processor.utils.ParisProcessorUtils
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeKind

internal class StyleCompanionPropertyInfoExtractor(override val processor: ParisProcessor) :
    SkyCompanionPropertyModelFactory<StyleCompanionPropertyInfo>(processor, Style::class.java) {

    override fun elementToModel(element: XFieldElement): StyleCompanionPropertyInfo? {
        // TODO Get Javadoc from field/method and add it to the generated methods

        if (!element.isStatic()) {
            logError(element) {
                "Fields annotated with @Style must be static."
            }
            return null
        }

        if (!element.isFinal()) {
            logError(element) {
                "Fields annotated with @Style must be final."
            }
            return null
        }

        val type = element.type
        if (!type.isInt() && !processor.memoizer.styleClassTypeX.isAssignableFrom(type) && !type.isError()) {
            // Note: if the type is non existent we ignore this error check so that users don't need to change their kapt configuration, they'll still
            // get a build error though not as explicit.
            logError(element) {
                "Fields annotated with @Style must implement com.airbnb.paris.styles.Style or be of type int (and refer to a style resource)."
            }
            return null
        }

        val style = element.toAnnotationBox(Style::class)!!.value
        val isDefault = style.isDefault

        val enclosingElement = element.enclosingTypeElement

        val elementName = element.name

        val formattedName = ParisProcessorUtils.reformatStyleFieldOrMethodName(elementName)

        val javadoc = JavaCodeBlock.of("@see \$T#\$N\n", enclosingElement.className, elementName)
        val kdoc = KotlinCodeBlock.of("@see %T.%N\n", enclosingElement.className.toKPoet(), elementName)

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
    element: XFieldElement,
    override val elementName: String,
    override val formattedName: String,
    override val javadoc: JavaCodeBlock,
    override val kdoc: KotlinCodeBlock,
    override val isDefault: Boolean = false
) : SkyCompanionPropertyModel(element), StyleInfo
