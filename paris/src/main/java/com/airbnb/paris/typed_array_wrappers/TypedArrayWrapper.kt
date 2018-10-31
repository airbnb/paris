package com.airbnb.paris.typed_array_wrappers

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.AnyRes
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleableRes
import com.airbnb.paris.R
import com.airbnb.paris.styles.Style

abstract class TypedArrayWrapper {

    companion object {
        /**
         * Attributes that set their value to @null are ignored during the TypedArray conversion.
         * For example if a style sets android:background to @null then the resulting TypedArray
         * won't include a background value at all. In the case of android:background the View
         * implementation defaults to null, for other attributes like android:textColor TextView
         * defaults to an actual color (black).
         *
         * However when setting a style programmatically ignoring null values doesn't work,
         * because the view could be in any state. For example it could already have a background.
         * To get around this problem Paris provides alternative resources which will be converted
         * to null when bound to views.
         */
        private val ALTERNATE_NULL_RESOURCE_IDS = setOf(
            R.anim.null_,
            R.array.null_,
            R.color.null_,
            R.drawable.null_,
            R.font.null_,
            R.string.null_
        )
    }

    protected fun isNullRes(@AnyRes res: Int): Boolean = res in ALTERNATE_NULL_RESOURCE_IDS

    abstract fun getIndexCount(): Int

    abstract fun getIndex(at: Int): Int

    abstract fun hasValue(index: Int): Boolean

    abstract fun getBoolean(@StyleableRes index: Int): Boolean

    @ColorInt
    abstract fun getColor(@StyleableRes index: Int): Int

    abstract fun getColorStateList(@StyleableRes index: Int): ColorStateList?

    @Px
    abstract fun getDimensionPixelSize(@StyleableRes index: Int): Int

    abstract fun getDrawable(@StyleableRes index: Int): Drawable?

    abstract fun getFloat(@StyleableRes index: Int): Float

    abstract fun getFraction(index: Int, base: Int, pbase: Int): Float

    abstract fun getFont(@StyleableRes index: Int): Typeface?

    abstract fun getInt(@StyleableRes index: Int): Int

    abstract fun getLayoutDimension(@StyleableRes index: Int): Int

    abstract fun getResourceId(@StyleableRes index: Int): Int

    abstract fun getString(@StyleableRes index: Int): String?

    abstract fun getText(@StyleableRes index: Int): CharSequence?

    abstract fun getTextArray(index: Int): Array<CharSequence>?

    abstract fun getStyle(index: Int): Style

    abstract fun recycle()
}
