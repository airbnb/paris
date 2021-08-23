package com.airbnb.paris.processor.android_resource_scanner

import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.compat.XConverters.toJavac
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.RFinder
import javax.lang.model.element.Element

internal class AndroidResourceScanner(val rFinder: RFinder, val processor: ParisProcessor) {

    private val rMap: Map<Int, XFieldElement> by lazy {
        val rElement = rFinder.requireR
        val r2Element = processor.processingEnv.findTypeElement("${rElement.packageName}.R2")
        val rToUse = r2Element ?: rElement

        mutableMapOf<Int, XFieldElement>().apply {
            putAll(getAllResources(rToUse, "style"))
            putAll(getAllResources(rToUse, "styleable"))
            putAll(getAllResources(rToUse, "attr"))
        }
    }

    private fun getAllResources(rElement: XTypeElement, resourceType: String): Map<Int, XFieldElement> {
        val resourceClass = processor.processingEnv.requireTypeElement("${rElement.qualifiedName}.$resourceType")
        // TODO: KSP does not yet implement a way to get constant value of fields
        // https://github.com/google/ksp/issues/579
        return resourceClass.getDeclaredFields().associateBy { fieldElement ->
            fieldElement.toJavac().constantValue as Int? ?: error("R value is null for $rElement $resourceType")
        }
    }


    /**
     * Returns the [AndroidResourceId] that is used as an annotation value of the given [Element]
     */
    fun getId(value: Int): AndroidResourceId? {
        val rElement = rMap[value] ?: return null

        return AndroidResourceId(
            value,
            rElement.enclosingElement.className,
            rElement.name
        )
    }
}