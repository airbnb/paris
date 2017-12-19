package com.airbnb.paris.processor

import com.airbnb.paris.processor.android_resource_scanner.*
import com.airbnb.paris.processor.framework.*
import javax.lang.model.element.*

abstract internal class ParisHelper(processor: ParisProcessor) : SkyHelper<ParisProcessor>(processor) {

    protected val RElement get() = processor.RFinder.element!!
    protected val defaultStyleNameFormat get() = processor.defaultStyleNameFormat

    private val resourceScanner = processor.resourceScanner

    protected fun getResourceId(annotation: Class<out Annotation>, element: Element, value: Int): AndroidResourceId {
        return resourceScanner.getId(annotation, element, value)
    }
}
