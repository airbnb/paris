package com.airbnb.paris

import android.support.annotation.VisibleForTesting

@VisibleForTesting
class StyleApplierUtils {

    companion object {

        private class DebugListener(val ignoredAttributeIndexes: IntArray?) : Style.DebugListener {

            /**
             * For each TypedArray processed during the application of a style we save the style
             * and the set of attribute indexes. We save the style because it may be a sub-style of
             * the parent and we'll need that information to display a user friendly error
             */
            val attributeIndexes = ArrayList<Pair<Style, Set<Int>?>>()

            override fun beforeTypedArrayProcessed(style: Style, typedArray: TypedArrayWrapper) {
                val pair = Pair(style, Companion.getAttributeIndexes(typedArray, ignoredAttributeIndexes))
                attributeIndexes.add(pair)
            }
        }

        @VisibleForTesting
        fun assertSameAttributes(applier: StyleApplier<*, *>, vararg styles: Style) {
            if (styles.size <= 1) {
                return
            }

            // These can be safely ignored since a value is always applied
            val attributesWithDefaultValue = applier.attributesWithDefaultValue()

            val styleReference = styles.first()
            val debugListenerReference = DebugListener(attributesWithDefaultValue)
            styleReference.debugListener = debugListenerReference
            applier.apply(styleReference)

            for (style in styles.drop(1)) {
                val debugListener = DebugListener(attributesWithDefaultValue)
                style.debugListener = debugListener
                applier.apply(style)

                val mismatchedStyle = getMismatchedStyles(styleReference, debugListenerReference.attributeIndexes,
                        style, debugListener.attributeIndexes)
                if (mismatchedStyle != null) {
                    val context = applier.view.context
                    val viewSimpleName = applier.view.javaClass.simpleName
                    val isSubStyle = mismatchedStyle.first.styleRes != styleReference.styleRes
                    val styleReferenceName = context.resources.getResourceEntryName(mismatchedStyle.first.styleRes)
                    val otherStyleName = context.resources.getResourceEntryName(mismatchedStyle.second.styleRes)
                    if (isSubStyle) {
                        val parentStyleReferenceName = context.resources.getResourceEntryName(styleReference.styleRes)
                        val otherParentStyleName = context.resources.getResourceEntryName(style.styleRes)
                        throw AssertionError("Styles listed in @Styleable must have the same attributes. \"$styleReferenceName\" (referenced in \"$parentStyleReferenceName\") and \"$otherStyleName\" (referenced in \"$otherParentStyleName\") linked to $viewSimpleName have different attributes.")
                    } else {
                        throw AssertionError("Styles listed in @Styleable must have the same attributes. \"$styleReferenceName\" and \"$otherStyleName\" linked to $viewSimpleName have different attributes.")
                    }
                }
            }
        }

        private fun getMismatchedStyles(style1: Style, attributeIndexes1: ArrayList<Pair<Style, Set<Int>?>>,
                                        style2: Style, attributeIndexes2: ArrayList<Pair<Style, Set<Int>?>>): Pair<Style, Style>? {
            val iterator1 = attributeIndexes1.listIterator()
            val iterator2 = attributeIndexes2.listIterator()

            while (iterator1.hasNext() && iterator2.hasNext()) {
                val pair1 = iterator1.next()
                val pair2 = iterator2.next()
                if (pair1.second != pair2.second) {
                    // The sets of attributes are mismatched, they could be from a sub-style so we
                    // return the styles from the attribute maps
                    return Pair(pair1.first, pair2.first)
                }
            }

            if (iterator1.hasNext() || iterator2.hasNext()) {
                // One parent style is the same as the other plus other attributes: mismatch
                return Pair(style1, style2)
            } else {
                return null
            }
        }

        private fun getAttributeIndexes(typedArray: TypedArrayWrapper, ignoredAttributeIndexes: IntArray?): Set<Int> {
            return (0..typedArray.getIndexCount() - 1)
                    .map { typedArray.getIndex(it) }
                    .filter { ignoredAttributeIndexes == null || !ignoredAttributeIndexes.contains(it) }
                    .toSet()
        }
    }
}
