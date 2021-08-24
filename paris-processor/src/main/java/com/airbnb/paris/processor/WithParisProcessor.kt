package com.airbnb.paris.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.compat.XConverters.toJavac
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.framework.WithJavaSkyProcessor
import kotlin.reflect.KClass

internal interface WithParisProcessor : WithJavaSkyProcessor {

    override val processor: ParisProcessor

    val RElement: XTypeElement? get() = processor.rFinder.element

    val defaultStyleNameFormat get() = processor.defaultStyleNameFormat

    val namespacedResourcesEnabled get() = processor.namespacedResourcesEnabled


    fun getResourceId(annotation: KClass<out Annotation>, element: XElement, value: Int): AndroidResourceId? {
        val resourceId = processor.resourceScanner.getId(annotation, element, value)
        if (resourceId == null) {
            logError(element) {
                "Could not retrieve Android resource ID from annotation."
            }
        }
        return resourceId
    }
}
