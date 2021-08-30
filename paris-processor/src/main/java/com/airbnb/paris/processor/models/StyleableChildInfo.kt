package com.airbnb.paris.processor.models

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.isMethod
import com.airbnb.paris.annotations.StyleableChild
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.framework.models.SkyFieldModelFactory
import com.airbnb.paris.processor.framework.models.SkyPropertyModel
import com.airbnb.paris.processor.utils.isFieldElement

// TODO Forward Javadoc to the generated functions/methods

internal class StyleableChildInfoExtractor(
    val parisProcessor: ParisProcessor
) : SkyFieldModelFactory<StyleableChildInfo>(parisProcessor, StyleableChild::class.java) {

    /**
     * @param element Represents a field annotated with @StyleableChild
     */
    override fun elementToModel(element: XElement): StyleableChildInfo? {

        val attr = element.getAnnotation(StyleableChild::class)?.value ?: error("@StyleableChild not found on $element")
        val styleableResId: AndroidResourceId
        try {
            styleableResId = parisProcessor.getResourceId(StyleableChild::class, element, attr.value) ?: return null
        } catch (e: Throwable) {
            parisProcessor.logError(element) {
                "Incorrectly typed @StyleableChild value parameter. (This usually happens when an R value doesn't exist.) $e ${e.message}"
            }
            return null
        }

        var defaultValueResId: AndroidResourceId? = null
        if (attr.defaultValue != -1) {
            try {
                defaultValueResId = parisProcessor.getResourceId(StyleableChild::class, element, attr.defaultValue) ?: return null
            } catch (e: Throwable) {
                parisProcessor.logError(element) {
                    "Incorrectly typed @StyleableChild defaultValue parameter. (This usually happens when an R value doesn't exist.)"
                }
                return null
            }
        }

        val model = StyleableChildInfo(
            element,
            styleableResId,
            defaultValueResId,
            parisProcessor.environment
        )

        val getter = model.getterElement
        if (getter.isMethod() && getter.isPrivate() || getter.isFieldElement() && getter.isProtected()) {
            parisProcessor.logError(element) {
                "Fields and properties annotated with @StyleableChild can't be private or protected."
            }
            return null
        }

        return model
    }
}

internal class StyleableChildInfo(
    element: XElement,
    val styleableResId: AndroidResourceId,
    val defaultValueResId: AndroidResourceId?,
    env: XProcessingEnv
) : SkyPropertyModel(element, env)
