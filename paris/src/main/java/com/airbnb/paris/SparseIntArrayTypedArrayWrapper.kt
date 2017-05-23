package com.airbnb.paris

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.SparseIntArray

internal class SparseIntArrayTypedArrayWrapper constructor(val resources: Resources, val attributeMap: SparseIntArray) : TypedArrayWrapper {

    override fun isNull(index: Int): Boolean {
        // TODO
        return false
    }

    override fun getIndexCount(): Int {
        return attributeMap.size()
    }

    override fun getIndex(at: Int): Int {
        return attributeMap.valueAt(at)
    }

    override fun hasValue(index: Int): Boolean {
        return attributeMap.get(index, -1) != -1
    }

    override fun getBoolean(index: Int, defValue: Boolean): Boolean {
        return resources.getBoolean(attributeMap[index])
    }

    override fun getColor(index: Int, defValue: Int): Int {
        return resources.getColor(attributeMap[index])
    }

    override fun getColorStateList(index: Int): ColorStateList {
        return resources.getColorStateList(attributeMap[index])
    }

    override fun getDimensionPixelSize(index: Int, defValue: Int): Int {
        return resources.getDimensionPixelSize(attributeMap[index])
    }

    override fun getDrawable(index: Int): Drawable {
        return resources.getDrawable(attributeMap[index])
    }

    override fun getFloat(index: Int, defValue: Float): Float {
        // TODO  getFraction?
        return resources.getFraction(attributeMap[index], 1, 1)
    }

    override fun getFraction(index: Int, base: Int, pbase: Int, defValue: Float): Float {
        return resources.getFraction(attributeMap[index], base, pbase)
    }

    override fun getInt(index: Int, defValue: Int): Int {
        return resources.getInteger(attributeMap[index])
    }

    override fun getLayoutDimension(index: Int, defValue: Int): Int {
        // TODO
        return resources.getDimensionPixelSize(attributeMap[index])
    }

    override fun getResourceId(index: Int, defValue: Int): Int {
        // TODO
        return resources.getInteger(attributeMap[index])
    }

    override fun getString(index: Int): String {
        return resources.getString(attributeMap[index])
    }

    override fun getText(index: Int): CharSequence {
        return resources.getText(attributeMap[index])
    }

    override fun getTextArray(index: Int): Array<CharSequence> {
        return resources.getTextArray(attributeMap[index])
    }

    override fun recycle() {
        attributeMap.clear()
    }
}