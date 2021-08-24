package com.airbnb.paris.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.framework.WithSkyProcessor

internal interface WithParisProcessor : WithSkyProcessor {

    override val processor: ParisProcessor

    val RElement: XTypeElement get() = processor.rFinder.requireR

    val defaultStyleNameFormat get() = processor.defaultStyleNameFormat

    val namespacedResourcesEnabled get() = processor.namespacedResourcesEnabled

    fun getStyleableResourceId(element: XElement, value: Int): AndroidResourceId? {
        val resourceId = processor.resourceScanner.getIdForStyleableValue(value)
        if (resourceId == null) {
            logError(element) {
                "Could not retrieve Android resource ID from annotation."
            }
        }
        return resourceId
    }

    fun getAnyResourceId(element: XElement, value: Int): AndroidResourceId? {
        val resourceId = processor.resourceScanner.getIdForAnyValue(value)
        if (resourceId == null) {
            logError(element) {
                "Could not retrieve Android resource ID from annotation."
            }
        }
        return resourceId
    }
}
