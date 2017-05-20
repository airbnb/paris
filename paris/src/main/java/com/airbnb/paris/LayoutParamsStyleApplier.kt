package com.airbnb.paris

import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams

open class LayoutParamsStyleApplier(view: View) : StyleApplier<LayoutParamsStyleApplier, View>(view) {

    companion object {
        var NOT_SET = -10

        fun ifSetElse(value: Int, ifNotSet: Int): Int {
            return if (value != NOT_SET) value else ifNotSet
        }

        fun isAnySet(vararg values: Int): Boolean {
            for (value in values) {
                if (value != NOT_SET) {
                    return true
                }
            }
            return false
        }
    }
    
    enum class Option : Style.Config.Option {
        IgnoreLayoutWidthAndHeight
    }

    override fun attributes(): IntArray {
        return R.styleable.LayoutParams
    }

    override fun processAttributes(style: Style, a: TypedArrayWrapper) {
        val ignoreLayoutWidthAndHeight = style.hasOption(Option.IgnoreLayoutWidthAndHeight)
        var width = NOT_SET
        var height = NOT_SET
        var margin = NOT_SET
        var marginBottom = NOT_SET
        var marginLeft = NOT_SET
        var marginRight = NOT_SET
        var marginTop = NOT_SET

        if (a.hasValue(R.styleable.LayoutParams_android_layout_width) && !ignoreLayoutWidthAndHeight) {
            width = a.getLayoutDimension(R.styleable.LayoutParams_android_layout_width, 0)
        }
        if (a.hasValue(R.styleable.LayoutParams_android_layout_height) && !ignoreLayoutWidthAndHeight) {
            height = a.getLayoutDimension(R.styleable.LayoutParams_android_layout_height, 0)
        }
        if (a.hasValue(R.styleable.LayoutParams_android_layout_margin)) {
            margin = a.getDimensionPixelSize(R.styleable.LayoutParams_android_layout_margin, 0)
        }
        if (a.hasValue(R.styleable.LayoutParams_android_layout_marginBottom)) {
            marginBottom = a.getDimensionPixelSize(R.styleable.LayoutParams_android_layout_marginBottom, 0)
        }
        if (a.hasValue(R.styleable.LayoutParams_android_layout_marginLeft)) {
            marginLeft = a.getDimensionPixelSize(R.styleable.LayoutParams_android_layout_marginLeft, 0)
        }
        if (a.hasValue(R.styleable.LayoutParams_android_layout_marginRight)) {
            marginRight = a.getDimensionPixelSize(R.styleable.LayoutParams_android_layout_marginRight, 0)
        }
        if (a.hasValue(R.styleable.LayoutParams_android_layout_marginTop)) {
            marginTop = a.getDimensionPixelSize(R.styleable.LayoutParams_android_layout_marginTop, 0)
        }

        if ((width != NOT_SET) xor (height != NOT_SET)) {
            throw IllegalArgumentException("Width and height must either both be set, or not be set at all. It can't be one and not the other.")
        }

        val isWidthHeightSet = width != NOT_SET // Height follows given the XOR condition above
        val isMarginSet = isAnySet(margin, marginBottom, marginLeft, marginRight, marginTop)

        if (isWidthHeightSet) {
            var params: LayoutParams? = view.layoutParams
            if (params == null) {
                params = if (isMarginSet) MarginLayoutParams(width, height) else LayoutParams(width, height)
            } else {
                params.width = width
                params.height = height
            }
            view.layoutParams = params
        }

        if (isMarginSet) {
            val marginParams = view.layoutParams as MarginLayoutParams
            if (margin != NOT_SET) {
                marginParams.setMargins(margin, margin, margin, margin)
            }
            marginParams.bottomMargin = ifSetElse(marginBottom, marginParams.bottomMargin)
            marginParams.leftMargin = ifSetElse(marginLeft, marginParams.leftMargin)
            marginParams.rightMargin = ifSetElse(marginRight, marginParams.rightMargin)
            marginParams.topMargin = ifSetElse(marginTop, marginParams.topMargin)
            view.layoutParams = marginParams
        }
    }
}