package com.airbnb.paris

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.annotation.Px
import android.support.annotation.StyleableRes

interface TypedArrayWrapper {

    fun getIndexCount(): Int

    fun getIndex(at: Int): Int

    fun getBoolean(@StyleableRes index: Int, defValue: Boolean): Boolean

    fun getColorStateList(@StyleableRes index: Int): ColorStateList

    @Px fun getDimensionPixelSize(@StyleableRes index: Int, @Px defValue: Int): Int

    fun getDrawable(@StyleableRes index: Int): Drawable

    fun getFloat(@StyleableRes index: Int, defValue: Float): Float

    fun getInt(@StyleableRes index: Int, defValue: Int): Int

    fun getLayoutDimension(@StyleableRes index: Int, defValue: Int): Int

    fun getResourceId(@StyleableRes index: Int, defValue: Int): Int

    fun getString(@StyleableRes index: Int): String

    fun recycle()
}