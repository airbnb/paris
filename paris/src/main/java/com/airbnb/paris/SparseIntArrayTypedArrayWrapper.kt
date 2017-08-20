package com.airbnb.paris

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable

internal class SparseIntArrayTypedArrayWrapper constructor(val resources: Resources, private val attrs: IntArray, private val attributeMap: Map<Int, Int>) : TypedArrayWrapper {

    private val attributes by lazy { attributeMap.keys.toList() }

    override fun isNull(index: Int): Boolean {
        // TODO
        return false
    }

    override fun getIndexCount(): Int = attributeMap.size

    override fun getIndex(at: Int): Int = attributes[at]

    override fun hasValue(index: Int): Boolean = attributeMap[attrs[index]] != null

    override fun getBoolean(index: Int, defValue: Boolean): Boolean =
            resources.getBoolean(attributeMap[index]!!)

    override fun getColor(index: Int, defValue: Int): Int = resources.getColor(attributeMap[index]!!)

    override fun getColorStateList(index: Int): ColorStateList =
            resources.getColorStateList(attributeMap[index]!!)

    override fun getDimensionPixelSize(index: Int, defValue: Int): Int =
            resources.getDimensionPixelSize(attributeMap[index]!!)

    override fun getDrawable(index: Int): Drawable = resources.getDrawable(attributeMap[attrs[index]]!!)

    override fun getFloat(index: Int, defValue: Float): Float {
        // TODO  getFraction?
        return resources.getFraction(attributeMap[index]!!, 1, 1)
    }

    override fun getFraction(index: Int, base: Int, pbase: Int, defValue: Float): Float =
            resources.getFraction(attributeMap[index]!!, base, pbase)

    override fun getInt(index: Int, defValue: Int): Int = resources.getInteger(attributeMap[index]!!)

    override fun getLayoutDimension(index: Int, defValue: Int): Int {
        // TODO
        return resources.getDimensionPixelSize(attributeMap[index]!!)
    }

    override fun getResourceId(index: Int, defValue: Int): Int {
        // TODO
        return resources.getInteger(attributeMap[index]!!)
    }

    override fun getString(index: Int): String = resources.getString(attributeMap[index]!!)

    override fun getText(index: Int): CharSequence = resources.getText(attributeMap[index]!!)

    override fun getTextArray(index: Int): Array<CharSequence> =
            resources.getTextArray(attributeMap[index]!!)

    override fun recycle() {
        //
    }
}
