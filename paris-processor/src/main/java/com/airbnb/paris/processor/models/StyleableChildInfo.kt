package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.StyleableChild
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.WithParisProcessor
import com.airbnb.paris.processor.abstractions.XElement
import com.airbnb.paris.processor.abstractions.XProcessingEnv
import com.airbnb.paris.processor.abstractions.isFieldElement
import com.airbnb.paris.processor.abstractions.isMethod
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.framework.models.SkyFieldModelFactory
import com.airbnb.paris.processor.framework.models.SkyPropertyModel

// TODO Forward Javadoc to the generated functions/methods

internal class StyleableChildInfoExtractor(
    override val processor: ParisProcessor
) : SkyFieldModelFactory<StyleableChildInfo>(processor, StyleableChild::class.java), WithParisProcessor {

    /**
     * @param element Represents a field annotated with @StyleableChild
     */
    override fun elementToModel(element: XElement): StyleableChildInfo? {

        val attr = element.toAnnotationBox(StyleableChild::class)?.value ?: error("@StyleableChild not found on $element")
        val styleableResId: AndroidResourceId
        try {
            styleableResId = getResourceId(StyleableChild::class.java, element, attr.value) ?: return null
        } catch (e: Throwable) {
            logError(element) {
                "Incorrectly typed @StyleableChild value parameter. (This usually happens when an R value doesn't exist.)"
            }
            return null
        }

        var defaultValueResId: AndroidResourceId? = null
        if (attr.defaultValue != -1) {
            try {
                defaultValueResId = getResourceId(StyleableChild::class.java, element, attr.defaultValue) ?: return null
            } catch (e: Throwable) {
                logError(element) {
                    "Incorrectly typed @StyleableChild defaultValue parameter. (This usually happens when an R value doesn't exist.)"
                }
                return null
            }
        }

        val model = StyleableChildInfo(
            processingEnv,
            element,
            styleableResId,
            defaultValueResId
        )

        val getter = model.getterElement
        if (getter.isMethod() && getter.isPrivate() || getter.isFieldElement() && getter.isProtected()) {
            logError(element) {
                "Fields and properties annotated with @StyleableChild can't be private or protected."
            }
            return null
        }

        return model
    }
}

internal class StyleableChildInfo(
    processingEnv: XProcessingEnv,
    element: XElement,
    val styleableResId: AndroidResourceId,
    val defaultValueResId: AndroidResourceId?
) : SkyPropertyModel(processingEnv, element)
