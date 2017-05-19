package com.airbnb.paris

import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.Drawable

class TypedArrayTypedArrayWrapper constructor(val typedArray: TypedArray) : TypedArrayWrapper {

    override fun getIndexCount(): Int {
        return typedArray.indexCount
    }

    override fun getIndex(at: Int): Int {
        return typedArray.getIndex(at)
    }

    override fun hasValue(index: Int): Boolean {
        return typedArray.hasValue(index)
    }

    override fun getBoolean(index: Int, defValue: Boolean): Boolean {
        return typedArray.getBoolean(index, defValue)
    }

    override fun getColorStateList(index: Int): ColorStateList {
        return typedArray.getColorStateList(index)
    }

    override fun getDimensionPixelSize(index: Int, defValue: Int): Int {
        return typedArray.getDimensionPixelSize(index, defValue)
    }

    override fun getDrawable(index: Int): Drawable {
        return typedArray.getDrawable(index)
    }

    override fun getFloat(index: Int, defValue: Float): Float {
        return typedArray.getFloat(index, defValue)
    }

    override fun getInt(index: Int, defValue: Int): Int {
        return typedArray.getInt(index, defValue)
    }

    override fun getLayoutDimension(index: Int, defValue: Int): Int {
        return typedArray.getLayoutDimension(index, defValue)
    }

    override fun getResourceId(index: Int, defValue: Int): Int {
        return typedArray.getResourceId(index, defValue)
    }

    override fun getString(index: Int): String {
        return typedArray.getString(index)
    }

    override fun getText(index: Int): CharSequence {
        return typedArray.getText(index)
    }

    override fun recycle() {
        typedArray.recycle()
    }
}