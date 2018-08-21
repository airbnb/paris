package com.airbnb.paris.typed_array_wrappers

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import com.airbnb.paris.styles.MultiStyle
import com.airbnb.paris.styles.Style

/**
 * @param wrappers These are assumed to have been created with the same styleable attribute list
 * @param styleableAttrs The styleable attribute list from which the [wrappers] were created
 */
internal class MultiTypedArrayWrapper constructor(
    private val wrappers: List<TypedArrayWrapper>,
    private val styleableAttrs: IntArray
) : TypedArrayWrapper() {

    private val styleableAttrIndexes by lazy { styleableAttrIndexToWrapperMap.keys.toList() }

    private val styleableAttrIndexToWrapperMap by lazy {
        val attrResToWrapperMap = HashMap<Int, MutableList<TypedArrayWrapper>>()

        for (wrapper in wrappers) {
            (0 until wrapper.getIndexCount()).forEach { at ->
                val index = wrapper.getIndex(at)

                // The lists of wrappers are meant for substyles where all styles corresponding to
                // a given attribute combined together across all the wrappers. For other attribute
                // types only the last wrapper is used
                if (!attrResToWrapperMap.containsKey(index)) {
                    attrResToWrapperMap[index] = mutableListOf(wrapper)
                } else {
                    attrResToWrapperMap[index]!!.add(wrapper)
                }
            }
        }

        attrResToWrapperMap
    }

    private fun getWrappers(index: Int): List<TypedArrayWrapper> =
        styleableAttrIndexToWrapperMap[index]!!

    private fun getWrapper(index: Int): TypedArrayWrapper = getWrappers(index).last()

    override fun getIndexCount(): Int = styleableAttrIndexToWrapperMap.size

    override fun getIndex(at: Int): Int = styleableAttrIndexes[at]

    override fun hasValue(index: Int): Boolean {
        return styleableAttrIndexToWrapperMap[index] != null
    }

    override fun getBoolean(index: Int): Boolean =
        getWrapper(index).getBoolean(index)

    override fun getColor(index: Int): Int =
        getWrapper(index).getColor(index)

    override fun getColorStateList(index: Int): ColorStateList? =
        getWrapper(index).getColorStateList(index)

    override fun getDimensionPixelSize(index: Int): Int =
        getWrapper(index).getDimensionPixelSize(index)

    override fun getDrawable(index: Int): Drawable? =
        getWrapper(index).getDrawable(index)

    override fun getFloat(index: Int): Float =
        getWrapper(index).getFloat(index)

    override fun getFraction(index: Int, base: Int, pbase: Int): Float =
        getWrapper(index).getFraction(index, base, pbase)

    override fun getFont(index: Int): Typeface? =
        getWrapper(index).getFont(index)

    override fun getInt(index: Int): Int =
        getWrapper(index).getInt(index)

    override fun getLayoutDimension(index: Int): Int =
        getWrapper(index).getLayoutDimension(index)

    override fun getResourceId(index: Int): Int =
        getWrapper(index).getResourceId(index)

    override fun getString(index: Int): String? =
        getWrapper(index).getString(index)

    override fun getText(index: Int): CharSequence? =
        getWrapper(index).getText(index)

    override fun getTextArray(index: Int): Array<CharSequence>? =
        getWrapper(index).getTextArray(index)

    override fun getStyle(index: Int): Style {
        val styles = getWrappers(index).map { it.getStyle(index) }
        return MultiStyle.fromStyles("a_MultiTypedArrayWrapper_MultiStyle", styles)
    }

    override fun recycle() {
        wrappers.forEach { it.recycle() }
    }
}
