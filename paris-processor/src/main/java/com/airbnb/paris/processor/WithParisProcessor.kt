package com.airbnb.paris.processor

import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.framework.WithSkyProcessor
import javax.lang.model.element.Element

internal interface WithParisProcessor : WithSkyProcessor {

    override val processor: ParisProcessor

    val RElement get() = processor.rFinder.element

    val defaultStyleNameFormat get() = processor.defaultStyleNameFormat

    fun getResourceId(annotation: Class<out Annotation>, element: Element, value: Int): AndroidResourceId? {
        val resourceId = processor.resourceScanner.getId(annotation, element, value)
        if (resourceId == null) {
            logError(element) {
                "Could not retrieve Android resource ID from annotation."
            }
        }
        return resourceId
    }
}
