package com.airbnb.paris.utils

import android.content.res.*

fun Int.toColorStateList(): ColorStateList =
        ColorStateList(arrayOf(intArrayOf()), intArrayOf(this))
