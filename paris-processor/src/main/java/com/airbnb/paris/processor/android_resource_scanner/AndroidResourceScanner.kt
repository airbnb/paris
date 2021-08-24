package com.airbnb.paris.processor.android_resource_scanner

import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.compat.XConverters.toJavac
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.RFinder
import com.squareup.javapoet.ClassName
import javax.lang.model.element.Element

internal class AndroidResourceScanner(val rFinder: RFinder, val processor: ParisProcessor) {



    // XProcessing does not expose a way to get all nested classes, so we manually define the possibilities
    private val animResourceMap: Map<Int, XFieldElement> by lazy { buildResourceMap("anim") }
    private val arrayResourceMap: Map<Int, XFieldElement> by lazy { buildResourceMap("array") }
    private val attrResourceMap: Map<Int, XFieldElement> by lazy { buildResourceMap("attr") }
    private val boolResourceMap: Map<Int, XFieldElement> by lazy { buildResourceMap("bool") }
    private val colorResourceMap: Map<Int, XFieldElement> by lazy { buildResourceMap("color") }
    private val dimenResourceMap: Map<Int, XFieldElement> by lazy { buildResourceMap("dimen") }
    private val drawableResourceMap: Map<Int, XFieldElement> by lazy { buildResourceMap("drawable") }
    private val fontResourceMap: Map<Int, XFieldElement> by lazy { buildResourceMap("font") }
    private val fractionResourceMap: Map<Int, XFieldElement> by lazy { buildResourceMap("fraction") }
    private val integerResourceMap: Map<Int, XFieldElement> by lazy { buildResourceMap("integer") }
    private val stringResourceMap: Map<Int, XFieldElement> by lazy { buildResourceMap("string") }
    private val styleResourceMap: Map<Int, XFieldElement> by lazy { buildResourceMap("style") }
    private val styleableResourceMap: Map<Int, XFieldElement> by lazy { buildResourceMap("styleable") }

    val allResources: List<Pair<Int, XFieldElement>>
        get() {
            return boolResourceMap.toList() +
                    integerResourceMap.toList() +
                    dimenResourceMap.toList() +
                    fractionResourceMap.toList() +
                    arrayResourceMap.toList() +
                    colorResourceMap.toList() +
                    fontResourceMap.toList() +
                    drawableResourceMap.toList() +
                    stringResourceMap.toList() +
                    styleResourceMap.toList() +
                    animResourceMap.toList() +
                    attrResourceMap.toList() +
                    styleableResourceMap.toList()
        }

    private fun buildResourceMap(resourceType: String): Map<Int, XFieldElement> {
        val rElement = rFinder.requireR
        val rToUse = rFinder.r2Element ?: rElement

        return mutableMapOf<Int, XFieldElement>().apply {
            putAll(getAllResources(rToUse, resourceType))
        }
    }

    private fun getAllResources(rElement: XTypeElement, resourceType: String): Map<Int, XFieldElement> {
        val resourceClass = processor.processingEnv.findTypeElement("${rElement.qualifiedName}.$resourceType")
        // Module might not have any resources of this type
            ?: return emptyMap()

        // TODO: KSP does not yet implement a way to get constant value of fields
        // https://github.com/google/ksp/issues/579
        return resourceClass.getDeclaredFields().associateBy { fieldElement ->
            // Most fields are final ints, which we can get the value of.
            // A few fields are int[] which we can ignore.
            fieldElement.toJavac().constantValue as? Int ?: run {
                println("No constant value for $resourceType $fieldElement ${fieldElement.type}")
                null
            }
        }.filterKeys { it != null } as Map<Int, XFieldElement>
    }


    /**
     * Returns the [AndroidResourceId] that is used as an annotation value of the given [Element]
     */
    fun getIdForStyleableValue(value: Int): AndroidResourceId? = getId(value, styleableResourceMap)

    private fun getId(value: Int, resourceMap: Map<Int, XFieldElement>): AndroidResourceId? {
        val rElement = resourceMap[value] ?: return null

        val isUsingR2 = rFinder.r2Element != null

        val resourceClassName: ClassName = if (isUsingR2) {
            val r2ClassName = rElement.enclosingElement.className
            ClassName.get(r2ClassName.packageName(), "R", r2ClassName.simpleName())
        } else {
            rElement.enclosingElement.className
        }

        return AndroidResourceId(
            value,
            resourceClassName,
            rElement.name
        )
    }

    fun getIdForAnyValue(value: Int): AndroidResourceId? {
        // sorted by priority of what has the best chance of being used. Avoiding instantiating any of these maps unless needed.
        return getId(value, boolResourceMap)
            ?: getId(value, integerResourceMap)
            ?: getId(value, dimenResourceMap)
            ?: getId(value, fractionResourceMap)
            ?: getId(value, arrayResourceMap)
            ?: getId(value, colorResourceMap)
            ?: getId(value, fontResourceMap)
            ?: getId(value, drawableResourceMap)
            ?: getId(value, stringResourceMap)
            ?: getId(value, styleResourceMap)
            ?: getId(value, animResourceMap)
            ?: getId(value, attrResourceMap)
            ?: getId(value, styleableResourceMap)
    }
}