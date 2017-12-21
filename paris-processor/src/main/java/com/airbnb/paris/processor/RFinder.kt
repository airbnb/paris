package com.airbnb.paris.processor

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.models.*
import javax.lang.model.element.*
import javax.lang.model.type.*

internal class RFinder {

    var element: TypeElement? = null

    fun processConfig(config: ParisConfig) {
        if (element != null) {
            return
        }

        var rType: TypeMirror?
        config.let {
            rType = getRTypeFromConfig(config)

            // TODO Move check to getRTypeFromConfig
            check(rType == null || rType!!.asTypeElement().simpleName.toString() == "R") {
                "@ParisConfig's rClass parameter is pointing to a non-R class"
            }

            rType?.let {
                element = it.asTypeElement()
            }
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
            element = elements.getTypeElement(it.className!!.enclosingClassName().reflectionName())
        }
    }

    fun processStyleables(styleablesInfo: List<StyleableInfo>) {
        if (element == null && styleablesInfo.isNotEmpty()) {
            styleablesInfo[0].let {
                var packageName = it.elementPackageName
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

        val voidType = elements.getTypeElement(Void::class.java.canonicalName).asType()
        return if (types.isSameType(voidType, rType)) {
            null
        } else {
            rType
        }
    }
}
