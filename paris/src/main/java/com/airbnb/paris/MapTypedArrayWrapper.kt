package com.airbnb.paris

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.AttrRes
import android.support.annotation.VisibleForTesting
import android.support.annotation.VisibleForTesting.PACKAGE_PRIVATE
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
@VisibleForTesting(otherwise = PACKAGE_PRIVATE)
class MapTypedArrayWrapper constructor(
        private val resources: Resources,
        private val styleableAttrs: IntArray,
        private val attrResToValueResMap: Map<Int, Any>) : TypedArrayWrapper() {

    private val styleableAttrIndexes by lazy {
        attrResToValueResMap.keys
                .map { styleableAttrs.indexOf(it) }
                .filter { it != -1 }
    }

    override fun isNull(index: Int): Boolean = isNullRes(getResourceId(index, 0))

    override fun getIndexCount(): Int = styleableAttrIndexes.size

    override fun getIndex(at: Int): Int = styleableAttrIndexes[at]

    override fun hasValue(index: Int): Boolean = styleableAttrIndexToValueRes(index) != null

    override fun getBoolean(index: Int, defValue: Boolean): Boolean =
            getValue(index) { resId -> resources.getBoolean(resId) }

    override fun getColor(index: Int, defValue: Int): Int {
        return getValue(index) { resId ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resources.getColor(resId, null)
            } else {
                resources.getColor(resId)
            }
        }
    }

    override fun getColorStateList(index: Int): ColorStateList {
        return getValue(index) { resId ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resources.getColorStateList(resId, null)
            } else {
                resources.getColorStateList(resId)
            }
        }
    }

    override fun getDimensionPixelSize(index: Int, defValue: Int): Int =
            getValue(index) { resId -> resources.getDimensionPixelSize(resId) }

    override fun getDrawable(index: Int): Drawable {
        return getValue(index) { resId ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                resources.getDrawable(resId, null)
            } else {
                resources.getDrawable(resId)
            }
        }
    }

    override fun getFloat(index: Int, defValue: Float): Float =
            getValue(index) { resId -> resources.getFloat(resId) }

    override fun getFraction(index: Int, base: Int, pbase: Int, defValue: Float): Float =
            getValue(index) { resId -> resources.getFraction(resId, base, pbase) }

    override fun getInt(index: Int, defValue: Int): Int =
            getValue(index) { resId -> resources.getInteger(resId) }

    override fun getLayoutDimension(index: Int, defValue: Int): Int =
            getValue(index) { resId -> resources.getLayoutDimension(resId) }

    override fun getResourceId(index: Int, defValue: Int): Int =
            getValue(index) { resId -> resId }

    override fun getString(index: Int): String =
            getValue(index) { resId -> resources.getString(resId) }

    override fun getText(index: Int): CharSequence =
            getValue(index) { resId -> resources.getText(resId) }

    override fun getTextArray(index: Int): Array<CharSequence> =
            getValue(index) { resId -> resources.getTextArray(resId) }

    override fun getStyle(index: Int): Style =
            getValue(index) { resId -> SimpleStyle(resId) }

    override fun recycle() {
        //
    }

    fun <T> getValue(index: Int): T {
        @Suppress("UNCHECKED_CAST")
        return styleableAttrIndexToValueRes(index)!! as T
    }

    private fun <T> getValue(index: Int, resourceGetter: (Int) -> T): T {
        val value = styleableAttrIndexToValueRes(index)!!
        return if (value is ResourceId) {
            resourceGetter(value.resId)
        } else {
            @Suppress("UNCHECKED_CAST")
            return value as T
        }
    }

    private fun styleableAttrIndexToAttrRes(styleableAttrIndex: Int): Int =
            styleableAttrs[styleableAttrIndex]

    private fun attrResToValueRes(@AttrRes attributeRes: Int): Any? =
            attrResToValueResMap[attributeRes]

    private fun styleableAttrIndexToValueRes(styleableAttrIndex: Int): Any? =
            attrResToValueRes(styleableAttrIndexToAttrRes(styleableAttrIndex))
}
