package com.airbnb.paris

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable

/**
 * @param wrappers These are assumed to have been created with the same styleable attribute list
 */
internal class MultiTypedArrayWrapper constructor(private val wrappers: List<TypedArrayWrapper>) : TypedArrayWrapper() {

    private val styleableAttrIndexes by lazy { styleableAttrIndexToWrapperMap.keys.toList() }

    private val styleableAttrIndexToWrapperMap by lazy {
        val attrResToWrapperMap = HashMap<Int, TypedArrayWrapper>()
        wrappers.forEach { wrapper ->
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
            isNullRes(styleableAttrIndexToWrapperMap[index]!!.getResourceId(index, 0))

    override fun getIndexCount(): Int = styleableAttrIndexToWrapperMap.size

    override fun getIndex(at: Int): Int = styleableAttrIndexes[at]

    override fun hasValue(index: Int): Boolean {
        val wrapper = styleableAttrIndexToWrapperMap[index]
        return wrapper != null && wrapper.hasValue(index)
    }

    override fun getBoolean(index: Int, defValue: Boolean): Boolean =
            styleableAttrIndexToWrapperMap[index]!!.getBoolean(index, defValue)

    override fun getColor(index: Int, defValue: Int): Int =
            styleableAttrIndexToWrapperMap[index]!!.getColor(index, defValue)

    override fun getColorStateList(index: Int): ColorStateList =
            styleableAttrIndexToWrapperMap[index]!!.getColorStateList(index)

    override fun getDimensionPixelSize(index: Int, defValue: Int): Int =
            styleableAttrIndexToWrapperMap[index]!!.getDimensionPixelSize(index, defValue)

    override fun getDrawable(index: Int): Drawable =
            styleableAttrIndexToWrapperMap[index]!!.getDrawable(index)

    override fun getFloat(index: Int, defValue: Float): Float =
            styleableAttrIndexToWrapperMap[index]!!.getFloat(index, defValue)

    override fun getFraction(index: Int, base: Int, pbase: Int, defValue: Float): Float =
            styleableAttrIndexToWrapperMap[index]!!.getFraction(index, base, pbase, defValue)

    override fun getInt(index: Int, defValue: Int): Int =
            styleableAttrIndexToWrapperMap[index]!!.getInt(index, defValue)

    override fun getLayoutDimension(index: Int, defValue: Int): Int =
            styleableAttrIndexToWrapperMap[index]!!.getLayoutDimension(index, defValue)

    override fun getResourceId(index: Int, defValue: Int): Int =
            if (isNull(index)) 0 else styleableAttrIndexToWrapperMap[index]!!.getResourceId(index, 0)

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
