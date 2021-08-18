package com.airbnb.paris.processor

import com.airbnb.paris.processor.abstractions.XElement
import com.airbnb.paris.processor.abstractions.javac.JavacElement
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.framework.WithJavaSkyProcessor

internal interface WithParisProcessor : WithJavaSkyProcessor {

    override val processor: ParisProcessor

    val RElement get() = processor.rFinder.element

    val defaultStyleNameFormat get() = processor.defaultStyleNameFormat

    val namespacedResourcesEnabled get() = processor.namespacedResourcesEnabled


    fun getResourceId(annotation: Class<out Annotation>, element: XElement, value: Int): AndroidResourceId? {
        val resourceId = processor.resourceScanner.getId(annotation, (element as JavacElement).element, value)
        if (resourceId == null) {
            logError(element) {
                "Could not retrieve Android resource ID from annotation."
            }
        }
        return resourceId
    }
}
