package com.airbnb.paris.utils

import android.os.Build
import android.view.View
import androidx.annotation.Px
import androidx.annotation.RequiresApi

fun View.setPaddingBottom(@Px px: Int) {
    this.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, px)
}

fun View.setPaddingEnd(@Px px: Int) {
    this.setPaddingRelative(this.paddingStart, this.paddingTop, px, this.paddingBottom)
}

fun View.setPaddingHorizontal(@Px px: Int) {
    this.setPadding(px, this.paddingTop, px, this.paddingBottom)
}

fun View.setPaddingLeft(@Px px: Int) {
    this.setPadding(px, this.paddingTop, this.paddingRight, this.paddingBottom)
}

fun View.setPaddingRight(@Px px: Int) {
    this.setPadding(this.paddingLeft, this.paddingTop, px, this.paddingBottom)
}

fun View.setPaddingStart(@Px px: Int) {
    this.setPaddingRelative(px, this.paddingTop, this.paddingEnd, this.paddingBottom)
}

fun View.setPaddingTop(@Px px: Int) {
    this.setPadding(this.paddingLeft, px, this.paddingRight, this.paddingBottom)
}

fun View.setPaddingVertical(@Px px: Int) {
    this.setPadding(this.paddingLeft, px, this.paddingRight, px)
}
