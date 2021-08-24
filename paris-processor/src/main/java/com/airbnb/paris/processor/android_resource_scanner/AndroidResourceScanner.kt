package com.airbnb.paris.processor.android_resource_scanner

import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.compat.XConverters.toJavac
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.RFinder
import com.squareup.javapoet.ClassName

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

    private fun buildResourceMap(resourceType: String): Map<Int, XFieldElement> {
        return mutableMapOf<Int, XFieldElement>().apply {
            // R2 is required because the default R class uses non unique values for the styleable entry fields; it uses the position of the
            // styleable attribute within the styleable array which is not unique. The R2 class on the other hand is able to get unique
            // values for each field - all we need is to be able to look up the field on the class again to get the resource name and the actual
            // value doesn't matter as long as it is unique for this module's compilation.
            putAll(getAllResources(rFinder.r2Element, resourceType))
        }
    }

    private fun getAllResources(rElement: XTypeElement, resourceType: String): Map<Int, XFieldElement> {
        val resourceClass = processor.processingEnv.findTypeElement("${rElement.qualifiedName}.$resourceType")
        // Module might not have any resources of this type
            ?: return emptyMap()


        @Suppress("UNCHECKED_CAST")
        return resourceClass.getDeclaredFields().associateBy { fieldElement ->
            // Most fields are final ints, which we can get the value of.
            // A few fields are int[] which we can ignore.
            // TODO: KSP does not yet implement a way to get constant value of fields
            // https://github.com/google/ksp/issues/579
            fieldElement.toJavac().constantValue as? Int
        }
            .filterKeys { it != null } as Map<Int, XFieldElement>
    }


    fun getIdForStyleableValue(value: Int): AndroidResourceId? = getId(value, styleableResourceMap)

    private fun getId(value: Int, resourceMap: Map<Int, XFieldElement>): AndroidResourceId? {
        val rElement = resourceMap[value] ?: return null

        // We used the R2 class in the annotation, we must convert to R to reference the field correctly in generated code
        val r2ClassName = rElement.enclosingElement.className
        val resourceClassName = ClassName.get(r2ClassName.packageName(), "R", r2ClassName.simpleName())

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