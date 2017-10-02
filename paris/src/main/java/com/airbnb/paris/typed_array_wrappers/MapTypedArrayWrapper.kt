package com.airbnb.paris.typed_array_wrappers

import android.content.res.*
import android.graphics.drawable.*
import android.os.*
import android.support.annotation.*
import com.airbnb.paris.*
import com.airbnb.paris.styles.*
import com.airbnb.paris.utils.*

/*
 * Lexicon:
 * Attribute resource id: R.attr.attribute
 * Styleable attributes: R.styleable.MyView
 * Styleable attribute index: R.styleable.MyView_attribute
 */
// TODO Add support for default values? Doesn't seem like we need it since this is only used internally
internal class MapTypedArrayWrapper constructor(
        private val resources: Resources,
        private val styleableAttrs: IntArray,
        private val attrResToValueResMap: Map<Int, Any>) : TypedArrayWrapper() {

    private val styleableAttrIndexes by lazy {
        attrResToValueResMap.keys
                .map { styleableAttrs.indexOf(it) }
                .filter { it != -1 }
    }

    override fun isNull(index: Int): Boolean = isNullRes(getResourceId(index))

    override fun getIndexCount(): Int = styleableAttrIndexes.size

    override fun getIndex(at: Int): Int = styleableAttrIndexes[at]

    override fun hasValue(index: Int): Boolean = styleableAttrIndexToValueRes(index) != null

    override fun getBoolean(index: Int): Boolean =
            getValue(index) { resId -> resources.getBoolean(resId) }

    override fun getColor(index: Int): Int {
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

    override fun getDimensionPixelSize(index: Int): Int =
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

    override fun getFloat(index: Int): Float =
            getValue(index) { resId -> resources.getFloat(resId) }

    override fun getFraction(index: Int, base: Int, pbase: Int): Float =
            getValue(index) { resId -> resources.getFraction(resId, base, pbase) }

    override fun getInt(index: Int): Int =
            getValue(index) { resId -> resources.getInteger(resId) }

    override fun getLayoutDimension(index: Int): Int =
            getValue(index) { resId -> resources.getLayoutDimension(resId) }

    override fun getResourceId(index: Int): Int =
            getValue(index) { resId -> resId }

    override fun getString(index: Int): String =
            getValue(index) { resId -> resources.getString(resId) }

    override fun getText(index: Int): CharSequence =
            getValue(index) { resId -> resources.getText(resId) }

    override fun getTextArray(index: Int): Array<CharSequence> =
            getValue(index) { resId -> resources.getTextArray(resId) }

    override fun getStyle(index: Int): Style =
            getValue(index) { resId -> ResourceStyle(resId) }

    override fun recycle() {
        //
    }

    private fun <T> getValue(index: Int, resourceGetter: (Int) -> T): T {
        val value = styleableAttrIndexToValueRes(index)!!
        @Suppress("UNCHECKED_CAST")
        return when (value) {
            is ResourceId -> resourceGetter(value.resId)
            is DpValue -> resources.dpToPx(value.dpValue) as T
            is ColorValue -> value.colorValue.toColorStateList() as T
            else -> {
                return value as T
            }
        }
    }

    private fun styleableAttrIndexToAttrRes(styleableAttrIndex: Int): Int =
            styleableAttrs[styleableAttrIndex]

    private fun attrResToValueRes(@AttrRes attributeRes: Int): Any? =
            attrResToValueResMap[attributeRes]

    private fun styleableAttrIndexToValueRes(styleableAttrIndex: Int): Any? =
            attrResToValueRes(styleableAttrIndexToAttrRes(styleableAttrIndex))
}
