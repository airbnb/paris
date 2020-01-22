package com.airbnb.paris.material.utils

import android.graphics.PorterDuff

object ViewUtils {

    fun parseTintMode(value: Int, defaultMode: PorterDuff.Mode): PorterDuff.Mode {
        return when (value) {
            3 -> PorterDuff.Mode.SRC_OVER
            5 -> PorterDuff.Mode.SRC_IN
            9 -> PorterDuff.Mode.SRC_ATOP
            14 -> PorterDuff.Mode.MULTIPLY
            15 -> PorterDuff.Mode.SCREEN
            16 -> PorterDuff.Mode.ADD
            else -> defaultMode
        }
    }
}