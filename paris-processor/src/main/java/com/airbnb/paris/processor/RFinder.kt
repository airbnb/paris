package com.airbnb.paris.processor

import androidx.room.compiler.processing.XAnnotationBox
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isVoid
import androidx.room.compiler.processing.isVoidObject
import com.airbnb.paris.annotations.ParisConfig
import com.airbnb.paris.processor.models.AttrInfo
import com.airbnb.paris.processor.models.StyleableChildInfo
import com.airbnb.paris.processor.models.StyleableInfo

internal class RFinder(val processor: ParisProcessor) {

    var element: XTypeElement? = null
        private set

    fun processConfig(config: XAnnotationBox<ParisConfig>) {
        if (element != null) {
            return
        }

        element = getRTypeFromConfig(config)
    }

    fun processResourceAnnotations(
        styleableChildrenInfo: List<StyleableChildInfo>,
        attrsInfo: List<AttrInfo>
    ) {
        // If using namespacedResources, an attribute might reference another module's R2 file, so we
        // skip this method of determining the R file.
        if (element != null || processor.namespacedResourcesEnabled) {
            return
        }

        val arbitraryResId = when {
            styleableChildrenInfo.isNotEmpty() -> styleableChildrenInfo[0].styleableResId
            attrsInfo.isNotEmpty() -> attrsInfo[0].styleableResId
            else -> null
        }
        arbitraryResId?.let {
            element = processor.environment.findTypeElement(it.className.enclosingClassName().reflectionName())
        }
    }

    fun processStyleables(styleablesInfo: List<StyleableInfo>) {
        if (element != null || styleablesInfo.isEmpty()) return

        styleablesInfo[0].let { styleableInfo ->
            var packageName = styleableInfo.elementPackageName
            while (packageName.isNotBlank()) {
                processor.environment.findTypeElement("$packageName.R")?.let {
                    element = it
                    return
                }
                val lastIndexOfDot = packageName.lastIndexOf('.')
                packageName = if (lastIndexOfDot > 0) {
                    packageName.substring(0, lastIndexOfDot)
                } else {
                    ""
                }
            }
        }
    }

    private fun getRTypeFromConfig(config: XAnnotationBox<ParisConfig>): XTypeElement? {
        val rType = config.getAsType("rClass")

        // Void is the default so check against that
        return if (rType == null || rType.isVoidObject() || rType.isVoid()) {
            null
        } else {
            val rTypeElement = rType.typeElement ?: return null
            if (rTypeElement.name != "R") {
                processor.logError(rTypeElement) {
                    "@ParisConfig's rClass parameter is pointing to a non-R class"
                }
                null
            } else {
                rTypeElement
            }
        }
    }
}
