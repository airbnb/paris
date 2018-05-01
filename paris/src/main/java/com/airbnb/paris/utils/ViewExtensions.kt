package com.airbnb.paris.utils

import android.os.Build
import android.support.annotation.Px
import android.support.annotation.RequiresApi
import android.view.View

fun View.setPaddingBottom(@Px px: Int) {
    this.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, px)
}

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun View.setPaddingStart(@Px px: Int) {
    this.setPaddingRelative(px, this.paddingTop, this.paddingEnd, this.paddingBottom)
}

fun View.setPaddingTop(@Px px: Int) {
    this.setPadding(this.paddingLeft, px, this.paddingRight, this.paddingBottom)
}

fun View.setPaddingVertical(@Px px: Int) {
    this.setPadding(this.paddingLeft, px, this.paddingRight, px)
}
