package com.airbnb.paris.typed_array_wrappers

import android.content.res.*
import android.graphics.drawable.*
import com.airbnb.paris.*
import com.airbnb.paris.styles.*

internal class TypedArrayTypedArrayWrapper constructor(
        private val typedArray: TypedArray) : TypedArrayWrapper() {

    override fun isNull(index: Int): Boolean = isNullRes(typedArray.getResourceId(index, 0))

    override fun getIndexCount(): Int = typedArray.indexCount

    override fun getIndex(at: Int): Int = typedArray.getIndex(at)

    override fun hasValue(index: Int): Boolean = typedArray.hasValue(index)

    override fun getBoolean(index: Int, defValue: Boolean): Boolean =
            typedArray.getBoolean(index, defValue)

    override fun getColor(index: Int, defValue: Int): Int = typedArray.getColor(index, defValue)

    override fun getColorStateList(index: Int): ColorStateList = typedArray.getColorStateList(index)

    override fun getDimensionPixelSize(index: Int, defValue: Int): Int =
            typedArray.getDimensionPixelSize(index, defValue)

    override fun getDrawable(index: Int): Drawable = typedArray.getDrawable(index)

    override fun getFloat(index: Int, defValue: Float): Float = typedArray.getFloat(index, defValue)

    override fun getFraction(index: Int, base: Int, pbase: Int, defValue: Float): Float =
            typedArray.getFraction(index, base, pbase, defValue)

    override fun getInt(index: Int, defValue: Int): Int = typedArray.getInt(index, defValue)

    override fun getLayoutDimension(index: Int, defValue: Int): Int =
            typedArray.getLayoutDimension(index, defValue)

    override fun getResourceId(index: Int, defValue: Int): Int =
            if (isNull(index)) 0 else typedArray.getResourceId(index, 0)

    override fun getString(index: Int): String = typedArray.getString(index)

    override fun getText(index: Int): CharSequence = typedArray.getText(index)

    override fun getTextArray(index: Int): Array<CharSequence> = typedArray.getTextArray(index)

    override fun getStyle(index: Int): Style = ResourceStyle(getResourceId(index, -1))

    override fun recycle() {
        typedArray.recycle()
    }
}
