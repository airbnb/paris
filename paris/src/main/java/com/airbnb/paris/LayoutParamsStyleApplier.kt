package com.airbnb.paris

import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams

open class LayoutParamsStyleApplier(view: View) : StyleApplier<View>(view) {

    enum class Option : Style.Config.Option {
        IgnoreLayoutWidthAndHeight
    }

    override fun attributes(): IntArray {
        return R.styleable.LayoutParams
    }

    override fun processAttributes(style: Style, a: TypedArrayWrapper) {
        val ignoreLayoutWidthAndHeight = style.hasOption(Option.IgnoreLayoutWidthAndHeight)
        var width = StyleUtils.NOT_SET
        var height = StyleUtils.NOT_SET
        var margin = StyleUtils.NOT_SET
        var marginBottom = StyleUtils.NOT_SET
        var marginLeft = StyleUtils.NOT_SET
        var marginRight = StyleUtils.NOT_SET
        var marginTop = StyleUtils.NOT_SET

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

        if ((width != StyleUtils.NOT_SET) xor (height != StyleUtils.NOT_SET)) {
            throw IllegalArgumentException("Width and height must either both be set, or not be set at all. It can't be one and not the other.")
        }

        val isWidthHeightSet = width != StyleUtils.NOT_SET // Height follows given the XOR condition above
        val isMarginSet = StyleUtils.isAnySet(margin, marginBottom, marginLeft, marginRight, marginTop)

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
            if (margin != StyleUtils.NOT_SET) {
                marginParams.setMargins(margin, margin, margin, margin)
            }
            marginParams.bottomMargin = StyleUtils.ifSetElse(marginBottom, marginParams.bottomMargin)
            marginParams.leftMargin = StyleUtils.ifSetElse(marginLeft, marginParams.leftMargin)
            marginParams.rightMargin = StyleUtils.ifSetElse(marginRight, marginParams.rightMargin)
            marginParams.topMargin = StyleUtils.ifSetElse(marginTop, marginParams.topMargin)
            view.layoutParams = marginParams
        }
    }
}