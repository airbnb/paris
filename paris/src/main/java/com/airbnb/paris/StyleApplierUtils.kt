package com.airbnb.paris

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.annotation.UiThread
import com.airbnb.paris.styles.Style
import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper

class StyleApplierUtils {

    class DebugListener(
        private val viewToStyles: HashMap<View, MutableSet<Style>>,
        private val styleToAttrNames: HashMap<Style, MutableSet<String>>
    ) : StyleApplier.DebugListener {

        private fun <K, V> getOrDefault(map: Map<K, V>, key: K, default: V): V {
            return if (map.containsKey(key)) {
                map[key]!!
            } else {
                default
            }
        }

        override fun processAttributes(
            view: View,
            style: Style,
            attributes: IntArray,
            attributesWithDefaultValue: IntArray?,
            typedArray: TypedArrayWrapper
        ) {
            val styles = getOrDefault(viewToStyles, view, HashSet())
            styles.add(style)
            viewToStyles[view] = styles

            val attrIndexes = getAttributeIndexes(typedArray, attributesWithDefaultValue)
            val newAttrNames = getAttrNames(view.context, attributes, attrIndexes)
            val attrNames = getOrDefault(styleToAttrNames, style, HashSet())
            attrNames.addAll(newAttrNames)
            styleToAttrNames[style] = attrNames
        }
    }

    companion object {

        internal fun getAttrNames(context: Context, attrs: IntArray, attrIndexes: Set<Int>) =
            attrIndexes.map { index ->
                try {
                    context.resources.getResourceEntryName(attrs[index])
                } catch (e: Resources.NotFoundException) {
                    // This can happen when the device SDK doesn't support the attribute. In that case we can still take it into account and make sure
                    // it's in all the style, but we can't get its name so we use a substitute (which must be unique).
                    "NotFoundException:id=${attrs[index]}"
                }
            }.toSet()

        /**
         * TODO Add comment, including the fact that an Activity context must be used on the Paris method
         */
        @JvmStatic
        @UiThread
        fun assertSameAttributes(applier: StyleApplier<*, *>, vararg parentStyles: Style) {
            if (parentStyles.size <= 1) {
                return
            }

            val viewToStyles = HashMap<View, MutableSet<Style>>()
            val styleToAttrNames = HashMap<Style, MutableSet<String>>()

            applier.debugListener = DebugListener(viewToStyles, styleToAttrNames)
            for (parentStyle in parentStyles) {
                applier.apply(parentStyle)
            }
            // Reset to null so the applier can still be used
            // TODO Explicitly set the applier to "debug mode" or something so it's clear it becomes non-functional
            applier.debugListener = null

            var hasError = false
            val errorBuilder = StringBuilder()
            for ((view, styles) in viewToStyles) {
                val allAttrNames = styles.flatMap { style ->
                    styleToAttrNames[style]!!
                }.toSet()

                for (style in styles) {
                    val missingAttrNames = allAttrNames.subtract(styleToAttrNames[style]!!)
                    if (missingAttrNames.isNotEmpty()) {
                        hasError = true
                        errorBuilder.append(getMissingStyleAttributesError(view, style, styles.minus(style), missingAttrNames))
                    }
                }
            }

            if (hasError) {
                throw AssertionError(errorBuilder)
            }
        }

        internal fun getMissingStyleAttributesError(view: View, style: Style, otherStyles: Set<Style>, missingAttrNames: Set<String>): String {
            val context = view.context
            val viewName = view.javaClass.simpleName
            val styleName = style.name(context)
            return """
                |The $viewName style "$styleName" is missing the following attributes:
                |${missingAttrNames.joinToString("\n") { "✕ $it" }}
                |It must declare the same attributes as the following styles:
                |${otherStyles.joinToString(", ") { it.name(context) }}
                |
                """.trimMargin()
        }

        internal fun getAttributeIndexes(typedArray: TypedArrayWrapper, ignoredAttributeIndexes: IntArray?): Set<Int> {
            return (0 until typedArray.getIndexCount())
                .map { typedArray.getIndex(it) }
                .filter { ignoredAttributeIndexes == null || !ignoredAttributeIndexes.contains(it) }
                .toSet()
        }
    }
}
