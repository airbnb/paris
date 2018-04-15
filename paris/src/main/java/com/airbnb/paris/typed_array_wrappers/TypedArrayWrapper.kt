package com.airbnb.paris.typed_array_wrappers

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.annotation.AnyRes
import android.support.annotation.ColorInt
import android.support.annotation.Px
import android.support.annotation.StyleableRes
import com.airbnb.paris.R
import com.airbnb.paris.styles.Style
import java.util.*

abstract class TypedArrayWrapper {

    companion object {
        /**
         * Unfortunately Android doesn't support reading @null resources from a style resource like it
         * does from an AttributeSet so this trickery is required
         */
        private val NULL_RESOURCE_IDS = HashSet(Arrays.asList(R.anim.null_, R.color.null_, R.drawable.null_))
    }

    protected fun isNullRes(@AnyRes res: Int): Boolean = res in NULL_RESOURCE_IDS

    abstract fun isNull(index: Int): Boolean

    abstract fun getIndexCount(): Int

    abstract fun getIndex(at: Int): Int

    abstract fun hasValue(index: Int): Boolean

    abstract fun getBoolean(@StyleableRes index: Int): Boolean

    @ColorInt abstract fun getColor(@StyleableRes index: Int): Int

    abstract fun getColorStateList(@StyleableRes index: Int): ColorStateList

    @Px abstract fun getDimensionPixelSize(@StyleableRes index: Int): Int

    abstract fun getDrawable(@StyleableRes index: Int): Drawable

    abstract fun getFloat(@StyleableRes index: Int): Float

    abstract fun getFraction(index: Int, base: Int, pbase: Int): Float

    abstract fun getInt(@StyleableRes index: Int): Int

    abstract fun getLayoutDimension(@StyleableRes index: Int): Int

    abstract fun getResourceId(@StyleableRes index: Int): Int

    abstract fun getString(@StyleableRes index: Int): String

    abstract fun getText(@StyleableRes index: Int): CharSequence

    abstract fun getTextArray(index: Int): Array<CharSequence>

    abstract fun getStyle(index: Int): Style

    abstract fun recycle()
}
