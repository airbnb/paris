package com.airbnb.paris.typed_array_wrappers

import android.content.res.*
import android.graphics.drawable.*
import com.airbnb.paris.*

/**
 * @param wrappers These are assumed to have been created with the same styleable attribute list
 */
internal class MultiTypedArrayWrapper constructor(
        private val wrappers: List<TypedArrayWrapper>) : TypedArrayWrapper() {

    private val styleableAttrIndexes by lazy { styleableAttrIndexToWrapperMap.keys.toList() }

    // TODO Start with the last one and don't even lookup the indexes for which we already have values?
    private val styleableAttrIndexToWrapperMap by lazy {
        val attrResToWrapperMap = HashMap<Int, TypedArrayWrapper>()
        for (wrapper in wrappers) {
            (0 until wrapper.getIndexCount()).forEach { at ->
                val index = wrapper.getIndex(at)
                if (wrapper.hasValue(index)) {
                    attrResToWrapperMap[index] = wrapper
                }
            }
        }
        attrResToWrapperMap
    }

    override fun isNull(index: Int): Boolean =
            isNullRes(styleableAttrIndexToWrapperMap[index]!!.getResourceId(index))

    override fun getIndexCount(): Int = styleableAttrIndexToWrapperMap.size

    override fun getIndex(at: Int): Int = styleableAttrIndexes[at]

    override fun hasValue(index: Int): Boolean {
        val wrapper = styleableAttrIndexToWrapperMap[index]
        return wrapper != null && wrapper.hasValue(index)
    }

    override fun getBoolean(index: Int): Boolean =
            styleableAttrIndexToWrapperMap[index]!!.getBoolean(index)

    override fun getColor(index: Int): Int =
            styleableAttrIndexToWrapperMap[index]!!.getColor(index)

    override fun getColorStateList(index: Int): ColorStateList =
            styleableAttrIndexToWrapperMap[index]!!.getColorStateList(index)

    override fun getDimensionPixelSize(index: Int): Int =
            styleableAttrIndexToWrapperMap[index]!!.getDimensionPixelSize(index)

    override fun getDrawable(index: Int): Drawable =
            styleableAttrIndexToWrapperMap[index]!!.getDrawable(index)

    override fun getFloat(index: Int): Float =
            styleableAttrIndexToWrapperMap[index]!!.getFloat(index)

    override fun getFraction(index: Int, base: Int, pbase: Int): Float =
            styleableAttrIndexToWrapperMap[index]!!.getFraction(index, base, pbase)

    override fun getInt(index: Int): Int =
            styleableAttrIndexToWrapperMap[index]!!.getInt(index)

    override fun getLayoutDimension(index: Int): Int =
            styleableAttrIndexToWrapperMap[index]!!.getLayoutDimension(index)

    override fun getResourceId(index: Int): Int =
            if (isNull(index)) 0 else styleableAttrIndexToWrapperMap[index]!!.getResourceId(index)

    override fun getString(index: Int): String =
            styleableAttrIndexToWrapperMap[index]!!.getString(index)

    override fun getText(index: Int): CharSequence =
            styleableAttrIndexToWrapperMap[index]!!.getText(index)

    override fun getTextArray(index: Int): Array<CharSequence> =
            styleableAttrIndexToWrapperMap[index]!!.getTextArray(index)

    override fun getStyle(index: Int): Style =
            styleableAttrIndexToWrapperMap[index]!!.getStyle(index)

    override fun recycle() {
        wrappers.forEach { it.recycle() }
    }
}
