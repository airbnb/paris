package com.airbnb.paris

import android.support.annotation.VisibleForTesting

@VisibleForTesting
class StyleApplierUtils {

    companion object {

        private class DebugListener : Style.DebugListener {

            val attributeIndexesList = ArrayList<Set<Int>?>()

            override fun beforeTypedArrayProcessed(typedArray: TypedArrayWrapper?) {
                attributeIndexesList.add(if (typedArray != null) Companion.getAttributeIndexes(typedArray) else null)
            }
        }

        @VisibleForTesting
        fun assertSameAttributes(applier: StyleApplier<*, *>, vararg styles: Style) {
            if (styles.size <= 1) {
                return
            }

            val styleReference = styles.first()
            val debugListenerReference = DebugListener()
            styleReference.debugListener = debugListenerReference
            applier.apply(styleReference)

            for (style in styles.drop(1)) {
                val testingListener = DebugListener()
                style.debugListener = testingListener
                applier.apply(style)

                if (debugListenerReference.attributeIndexesList != testingListener.attributeIndexesList) {
                    val context = applier.view.context
                    val viewSimpleName = applier.view.javaClass.simpleName
                    // Assumes these styles have a style resource since they come from @Styleable
                    val styleReferenceName = context.resources.getResourceEntryName(styleReference.styleRes)
                    val otherStyleName = context.resources.getResourceEntryName(style.styleRes)
                    throw AssertionError("Styles listed in @Styleable must have the same attributes. \"$styleReferenceName\" and \"$otherStyleName\" linked to $viewSimpleName have different attributes.")
                }
            }
        }

        private fun getAttributeIndexes(typedArray: TypedArrayWrapper): Set<Int> {
            return (0..typedArray.getIndexCount()-1).map {
                typedArray.getIndex(it)
            }.toSet()
        }
    }
}
