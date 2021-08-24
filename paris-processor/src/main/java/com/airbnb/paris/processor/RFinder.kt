package com.airbnb.paris.processor

import androidx.room.compiler.processing.XAnnotationBox
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isVoid
import androidx.room.compiler.processing.isVoidObject
import com.airbnb.paris.annotations.ParisConfig

internal class RFinder(override val processor: ParisProcessor) : WithParisProcessor {

    private var element: XTypeElement? = null
        private set

    val requireR: XTypeElement
        get() = element
            ?: error("Unable to locate R class. Please annotate an arbitrary package with @ParisConfig and set the rClass parameter to the R class.")

    val r2Element: XTypeElement by lazy {
        processor.processingEnv.findTypeElement("${requireR.packageName}.R2")
            ?: error("No R2 class was found. The Butterknife Gradle plugin must be applied to this module so that an R2 class is generated.")
    }

    val hasProcessedConfiguration: Boolean get() = element != null

    fun processConfig(config: XAnnotationBox<ParisConfig>) {
        if (hasProcessedConfiguration) {
            error("Paris config was already processed ")
        }

        element = getRTypeFromConfig(config) ?: error("No R class found in Paris config")
    }

    private fun getRTypeFromConfig(config: XAnnotationBox<ParisConfig>): XTypeElement? {
        val rType = config.getAsType("rClass")

        // Void is the default so check against that
        return if (rType == null || rType.isVoidObject() || rType.isVoid()) {
            null
        } else {
            val rTypeElement = rType.typeElement ?: return null
            if (rTypeElement.name != "R") {
                logError(rTypeElement) {
                    "@ParisConfig's rClass parameter is pointing to a non-R class"
                }
                null
            } else {
                rTypeElement
            }
        }
    }
}
