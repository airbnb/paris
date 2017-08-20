package com.airbnb.paris

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.AttrRes
import android.support.annotation.VisibleForTesting
import com.airbnb.paris.utils.getFloat
import com.airbnb.paris.utils.getLayoutDimension

/*
 * Lexicon:
 * Attribute resource id: R.attr.attribute
 * Styleable attributes: R.styleable.MyView
 * Styleable attribute index: R.styleable.MyView_attribute
 */
// TODO Add support for default values? Doesn't seem like we need it since this is only used internally
// TODO There seems to be a bug where if this class is internal it can't be accessed by androidTests
@VisibleForTesting
class MapTypedArrayWrapper constructor(
        private val resources: Resources,
        private val styleableAttrs: IntArray,
        private val attrResToValueResMap: Map<Int, Int>) : TypedArrayWrapper() {

    private val attributes by lazy { attrResToValueResMap.keys.toList() }

    private fun styleableAttrIndexToAttrRes(styleableAttrIndex: Int): Int =
            styleableAttrs[styleableAttrIndex]

    private fun attrResToValueRes(@AttrRes attributeRes: Int): Int? =
            attrResToValueResMap[attributeRes]

    private fun styleableAttrIndexToValueRes(styleableAttrIndex: Int): Int? =
            attrResToValueRes(styleableAttrIndexToAttrRes(styleableAttrIndex))

    override fun isNull(index: Int): Boolean = isNullRes(getResourceId(index, 0))

    override fun getIndexCount(): Int = attrResToValueResMap.size

    override fun getIndex(at: Int): Int = attributes[at]

    override fun hasValue(index: Int): Boolean = styleableAttrIndexToValueRes(index) != null

    override fun getBoolean(index: Int, defValue: Boolean): Boolean =
            resources.getBoolean(styleableAttrIndexToValueRes(index)!!)

    override fun getColor(index: Int, defValue: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColor(styleableAttrIndexToValueRes(index)!!, null)
        } else {
            resources.getColor(styleableAttrIndexToValueRes(index)!!)
        }
    }

    override fun getColorStateList(index: Int): ColorStateList =
            resources.getColorStateList(styleableAttrIndexToValueRes(index)!!)

    override fun getDimensionPixelSize(index: Int, defValue: Int): Int =
            resources.getDimensionPixelSize(styleableAttrIndexToValueRes(index)!!)

    override fun getDrawable(index: Int): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resources.getDrawable(styleableAttrIndexToValueRes(index)!!, null)
        } else {
            resources.getDrawable(styleableAttrIndexToValueRes(index)!!)
        }
    }

    override fun getFloat(index: Int, defValue: Float): Float =
            resources.getFloat(styleableAttrIndexToValueRes(index)!!)

    override fun getFraction(index: Int, base: Int, pbase: Int, defValue: Float): Float =
            resources.getFraction(styleableAttrIndexToValueRes(index)!!, base, pbase)

    override fun getInt(index: Int, defValue: Int): Int =
            resources.getInteger(styleableAttrIndexToValueRes(index)!!)

    override fun getLayoutDimension(index: Int, defValue: Int): Int =
            resources.getLayoutDimension(styleableAttrIndexToValueRes(index)!!)

    override fun getResourceId(index: Int, defValue: Int): Int =
            styleableAttrIndexToValueRes(index)!!

    override fun getString(index: Int): String =
            resources.getString(styleableAttrIndexToValueRes(index)!!)

    override fun getText(index: Int): CharSequence =
            resources.getText(styleableAttrIndexToValueRes(index)!!)

    override fun getTextArray(index: Int): Array<CharSequence> =
            resources.getTextArray(styleableAttrIndexToValueRes(index)!!)

    override fun recycle() {
        //
    }
}
