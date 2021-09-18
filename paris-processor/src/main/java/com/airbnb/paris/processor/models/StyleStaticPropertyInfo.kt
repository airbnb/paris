package com.airbnb.paris.processor.models

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.compat.XConverters.toJavac
import androidx.room.compiler.processing.isInt
import com.airbnb.paris.annotations.Style
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.framework.JavaCodeBlock
import com.airbnb.paris.processor.framework.KotlinCodeBlock
import com.airbnb.paris.processor.framework.models.SkyStaticPropertyModel
import com.airbnb.paris.processor.framework.models.SkyStaticPropertyModelFactory
import com.airbnb.paris.processor.framework.toKPoet
import com.airbnb.paris.processor.utils.ParisProcessorUtils
import com.airbnb.paris.processor.utils.enclosingElementIfCompanion
import com.airbnb.paris.processor.utils.isErrorFixed

internal class StyleStaticPropertyInfoExtractor(val parisProcessor: ParisProcessor) :
    SkyStaticPropertyModelFactory<StyleStaticPropertyInfo>(parisProcessor, Style::class.java) {

    override fun filter(element: XElement): Boolean {
        if ((element as? XFieldElement)?.isStatic() == false) {
            // Style annotations must be only on static properties, but they are filtered out before they are processed,
            // so in order to warn on this error we must do it here.
            parisProcessor.logError(element) {
                "Fields annotated with @Style must be static."
            }
        }
        return super.filter(element)
    }

    override fun elementToModel(element: XElement): StyleStaticPropertyInfo? {
        // TODO Get Javadoc from field/method and add it to the generated methods

        val (type, elementName, enclosingElement) = when (element) {
            is XFieldElement -> {
                Triple(element.type, element.name, element.enclosingElement as XTypeElement)
            }
            is XMethodElement -> {
                Triple(element.returnType, element.name, element.enclosingElement as XTypeElement)
            }
            else -> {
                parisProcessor.logError(element) {
                    "Unsupported companion property element type $element is ${element.javaClass}"
                }
                return null
            }
        }

        if (!type.isInt() && !parisProcessor.memoizer.styleClassTypeX.isAssignableFrom(type) && !type.isErrorFixed) {
            // Note: if the type is non existent we ignore this error check so that users don't need to change their kapt configuration, they'll still
            // get a build error though not as explicit.
            parisProcessor.logError(element) {
                "Fields annotated with @Style must implement com.airbnb.paris.styles.Style or be of type int (and refer to a style resource). Found type $type}"
            }
            return null
        }

        val style = element.getAnnotation(Style::class)!!.value
        val isDefault = style.isDefault

        val formattedName = ParisProcessorUtils.reformatStyleFieldOrMethodName(elementName)

        val javadoc = JavaCodeBlock.of("@see \$T#\$N\n", enclosingElement.className, elementName)
        val kdoc = KotlinCodeBlock.of("@see %T.%N\n", enclosingElement.enclosingElementIfCompanion.className.toKPoet(), elementName)

        val propertyInfo = StyleStaticPropertyInfo(
            env = parisProcessor.environment,
            element = element,
            elementName = elementName,
            formattedName = formattedName,
            javadoc = javadoc,
            kdoc = kdoc,
            isDefault = isDefault
        )

        when (val getterElement = propertyInfo.getterElement) {
            is XFieldElement -> {
                if (!getterElement.isFinal()) {
                    parisProcessor.logError(getterElement) {
                        "Fields annotated with @Style must be final."
                    }
                    return null
                }

                // skipping the private/protected check, as JvmStatic companion properties are private without an easy way to easy the original
                // kotlin property visibility.
            }
            is XMethodElement -> {
                if (getterElement.isPrivate() || getterElement.isProtected()) {
                    parisProcessor.logError(getterElement) {
                        "Fields annotated with @Style can't be private or protected."
                    }
                    return null
                }
            }
        }

        return propertyInfo
    }
}

internal class StyleStaticPropertyInfo(
    env: XProcessingEnv,
    element: XElement,
    override val elementName: String,
    override val formattedName: String,
    override val javadoc: JavaCodeBlock,
    override val kdoc: KotlinCodeBlock,
    override val isDefault: Boolean = false
) : SkyStaticPropertyModel(element, env), StyleInfo
