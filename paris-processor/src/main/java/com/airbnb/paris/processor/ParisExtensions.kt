package com.airbnb.paris.processor

import com.airbnb.paris.processor.android_resource_scanner.*
import javax.lang.model.element.*

internal val RElement get() = ParisProcessor.INSTANCE.rFinder.element!!

internal val defaultStyleNameFormat get() = ParisProcessor.INSTANCE.defaultStyleNameFormat

internal fun getResourceId(annotation: Class<out Annotation>, element: Element, value: Int): AndroidResourceId {
    return ParisProcessor.INSTANCE.resourceScanner.getId(annotation, element, value)
}
