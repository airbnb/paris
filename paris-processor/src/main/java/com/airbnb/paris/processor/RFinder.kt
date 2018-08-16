package com.airbnb.paris.processor

import com.airbnb.paris.annotations.ParisConfig
import com.airbnb.paris.processor.models.AttrInfo
import com.airbnb.paris.processor.models.StyleableChildInfo
import com.airbnb.paris.processor.models.StyleableInfo
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror

internal class RFinder(override val processor: ParisProcessor) : WithParisProcessor {

    var element: TypeElement? = null

    fun processConfig(config: ParisConfig) {
        if (element != null) {
            return
        }

        getRTypeFromConfig(config)?.let {
            element = it.asTypeElement()
        }
    }

    fun processResourceAnnotations(
        styleableChildrenInfo: List<StyleableChildInfo>,
        attrsInfo: List<AttrInfo>
    ) {
        if (element != null) {
            return
        }

        val arbitraryResId = when {
            styleableChildrenInfo.isNotEmpty() -> styleableChildrenInfo[0].styleableResId
            attrsInfo.isNotEmpty() -> attrsInfo[0].styleableResId
            else -> null
        }
        arbitraryResId?.let {
            element = elements.getTypeElement(it.className.enclosingClassName().reflectionName())
        }
    }

    fun processStyleables(styleablesInfo: List<StyleableInfo>) {
        if (element == null && styleablesInfo.isNotEmpty()) {
            styleablesInfo[0].let { styleableInfo ->
                var packageName = styleableInfo.elementPackageName
                while (packageName.isNotBlank()) {
                    elements.getTypeElement("$packageName.R")?.let {
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
    }

    private fun getRTypeFromConfig(config: ParisConfig): TypeMirror? {
        var rType: TypeMirror? = null
        try {
            config.rClass
        } catch (mte: MirroredTypeException) {
            rType = mte.typeMirror
        }

        // Void is the default so check against that
        val voidType = elements.getTypeElement(Void::class.java.canonicalName).asType()
        return if (types.isSameType(voidType, rType)) {
            null
        } else {
            if (rType != null && rType.asTypeElement().simpleName.toString() != "R") {
                logError {
                    "@ParisConfig's rClass parameter is pointing to a non-R class"
                }
                null
            } else {
                rType
            }
        }
    }
}
