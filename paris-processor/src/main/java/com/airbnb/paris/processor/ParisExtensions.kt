package com.airbnb.paris.processor

import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.framework.logError
import javax.lang.model.element.Element

internal val RElement get() = ParisProcessor.INSTANCE.rFinder.element

internal val defaultStyleNameFormat get() = ParisProcessor.INSTANCE.defaultStyleNameFormat

internal fun getResourceId(annotation: Class<out Annotation>, element: Element, value: Int): AndroidResourceId? {
    val resourceId = ParisProcessor.INSTANCE.resourceScanner.getId(annotation, element, value)
    if (resourceId == null) {
        logError(element) {
            "Could not retrieve Android resource ID from annotation."
        }
    }
    return resourceId
}
