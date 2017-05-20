package com.airbnb.paris

import android.support.annotation.Px
import android.view.View

fun View.setPadding(@Px px: Int) {
    this.setPadding(px, px, px, px)
}

fun View.setPaddingBottom(@Px px: Int) {
    this.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, px)
}

fun View.setPaddingLeft(@Px px: Int) {
    this.setPadding(px, this.paddingTop, this.paddingRight, this.paddingBottom)
}

fun View.setPaddingRight(@Px px: Int) {
    this.setPadding(this.paddingLeft, this.paddingTop, px, this.paddingBottom)
}

fun View.setPaddingTop(@Px px: Int) {
    this.setPadding(this.paddingLeft, px, this.paddingRight, this.paddingBottom)
}
