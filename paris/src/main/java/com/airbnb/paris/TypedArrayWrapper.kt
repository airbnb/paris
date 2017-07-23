package com.airbnb.paris

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.Px
import android.support.annotation.StyleableRes

// TODO  Remove support for default values since not all implementations can do it?
interface TypedArrayWrapper {

    fun isNull(index: Int): Boolean

    fun getIndexCount(): Int

    fun getIndex(at: Int): Int

    fun hasValue(index: Int): Boolean

    fun getBoolean(@StyleableRes index: Int, defValue: Boolean): Boolean

    @ColorInt fun getColor(@StyleableRes index: Int, @ColorInt defValue: Int): Int

    fun getColorStateList(@StyleableRes index: Int): ColorStateList

    @Px fun getDimensionPixelSize(@StyleableRes index: Int, @Px defValue: Int): Int

    fun getDrawable(@StyleableRes index: Int): Drawable

    fun getFloat(@StyleableRes index: Int, defValue: Float): Float

    fun getFraction(index: Int, base: Int, pbase: Int, defValue: Float): Float

    fun getInt(@StyleableRes index: Int, defValue: Int): Int

    fun getLayoutDimension(@StyleableRes index: Int, defValue: Int): Int

    fun getResourceId(@StyleableRes index: Int, defValue: Int): Int

    fun getString(@StyleableRes index: Int): String

    fun getText(@StyleableRes index: Int): CharSequence

    fun getTextArray(index: Int): Array<CharSequence>

    fun recycle()
}
