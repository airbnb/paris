package com.airbnb.paris.typed_array_wrappers

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.AttrRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import com.airbnb.paris.attribute_values.ColorValue
import com.airbnb.paris.attribute_values.DpValue
import com.airbnb.paris.attribute_values.ResourceId
import com.airbnb.paris.attribute_values.Styles
import com.airbnb.paris.styles.MultiStyle
import com.airbnb.paris.styles.ResourceStyle
import com.airbnb.paris.styles.Style
import com.airbnb.paris.utils.dpToPx
import com.airbnb.paris.utils.getFloat
import com.airbnb.paris.utils.getFont
import com.airbnb.paris.utils.getLayoutDimension
import com.airbnb.paris.utils.toColorStateList

/*
 * Lexicon:
 * Attribute resource id: R.attr.attribute
 * Styleable attributes: R.styleable.MyView
 * Styleable attribute index: R.styleable.MyView_attribute
 */
internal class MapTypedArrayWrapper constructor(
    private val context: Context,
    private val styleableAttrs: IntArray,
    private val attrResToValueMap: Map<Int, Any?>
) : TypedArrayWrapper() {

    private val resources = context.resources
    private val theme = context.theme

    private val styleableAttrIndexes by lazy {
        attrResToValueMap.keys
            .map { styleableAttrs.indexOf(it) }
            // TODO Is this filtering necessary? If so document it
            .filter { it != -1 }
    }

    override fun getIndexCount(): Int = styleableAttrIndexes.size

    override fun getIndex(at: Int): Int = styleableAttrIndexes[at]

    override fun hasValue(index: Int): Boolean {
        return attrResToValueMap.containsKey(styleableAttrIndexToAttrRes(index))
    }

    override fun getBoolean(index: Int): Boolean =
        getValue(index, { resId -> resources.getBoolean(resId) })

    override fun getColor(index: Int): Int {
        return getValue(index, { resId ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resources.getColor(resId, theme)
            } else {
                @Suppress("DEPRECATION")
                resources.getColor(resId)
            }
        })
    }

    override fun getColorStateList(index: Int): ColorStateList? {
        return getValue(
            index,
            { resId ->
                when {
                    isNullRes(resId) -> null
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> resources.getColorStateList(
                        resId,
                        theme
                    )
                    else -> {
                        AppCompatResources.getColorStateList(context, resId)
                    }
                }
            },
            { colorValue -> colorValue.colorValue.toColorStateList() }
        )
    }

    override fun getDimensionPixelSize(index: Int): Int =
        getValue(index, { resId -> resources.getDimensionPixelSize(resId) })

    override fun getDrawable(index: Int): Drawable? {
        return getValue(index, { resId ->
            when {
                isNullRes(resId) -> null
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> ResourcesCompat.getDrawable(
                    resources,
                    resId,
                    theme
                )
                else -> {
                    ResourcesCompat.getDrawable(resources, resId, null)
                }
            }
        })
    }

    override fun getFloat(index: Int): Float =
        getValue(index, { resId ->
            ResourcesCompat.getFloat(resources, resId)
        })

    override fun getFont(index: Int): Typeface? {
        val value = styleableAttrIndexToValueRes(index)
        return when (value) {
            is String -> Typeface.create(value, Typeface.NORMAL)
            is ResourceId -> if (isNullRes(value.resId)) null else context.getFont(value.resId)
            else -> return value as Typeface?
        }
    }

    override fun getFraction(index: Int, base: Int, pbase: Int): Float =
        getValue(index, { resId -> resources.getFraction(resId, base, pbase) })

    override fun getInt(index: Int): Int =
        getValue(index, { resId -> resources.getInteger(resId) })

    override fun getLayoutDimension(index: Int): Int =
        getValue(index, { resId -> resources.getLayoutDimension(resId) })

    override fun getResourceId(index: Int): Int {
        val resId = getValue(index, { resId -> resId })
        return if (isNullRes(resId)) {
            // One of our alternate null resources was used here so we return 0 instead of an
            // existing resource
            0
        } else {
            resId
        }
    }

    override fun getString(index: Int): String? =
        getValue(index, { resId -> resources.getString(resId) })

    override fun getText(index: Int): CharSequence? =
        getValue(index, { resId -> resources.getText(resId) })

    override fun getTextArray(index: Int): Array<CharSequence>? =
        getValue(index, { resId -> resources.getTextArray(resId) })

    override fun getStyle(index: Int): Style =
        getValue<Style>(index, { resId -> ResourceStyle(resId) })

    override fun recycle() {
        // TODO Clear resources and throw if other methods are called after this
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getValue(
        index: Int,
        resourceGetter: (Int) -> T,
        colorValueGetter: ((ColorValue) -> T) = { it.colorValue as T }
    ): T {
        val value = styleableAttrIndexToValueRes(index)
        return when (value) {
            is ColorValue -> colorValueGetter(value)
            is DpValue -> resources.dpToPx(value.dpValue) as T
            is ResourceId -> resourceGetter(value.resId)
            is Styles -> MultiStyle.fromStyles("a_MapTypedArrayWrapper_MultiStyle", value.list) as T
            else -> {
                return value as T
            }
        }
    }

    private fun styleableAttrIndexToAttrRes(styleableAttrIndex: Int): Int =
        styleableAttrs[styleableAttrIndex]

    private fun attrResToValueRes(@AttrRes attributeRes: Int): Any? =
        attrResToValueMap[attributeRes]

    private fun styleableAttrIndexToValueRes(styleableAttrIndex: Int): Any? =
        attrResToValueRes(styleableAttrIndexToAttrRes(styleableAttrIndex))
}
