package com.airbnb.paris

import android.content.res.TypedArray
import android.support.annotation.StyleRes
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams

import com.google.auto.value.AutoValue

@AutoValue
abstract class LayoutParamsStyle : BaseStyle<View>() {

    enum class Option : Style.Config.Option {
        IgnoreLayoutWidthAndHeight
    }

    private var loaded = false

    private var width = StyleUtils.NOT_SET
    private var height = StyleUtils.NOT_SET
    private var margin = StyleUtils.NOT_SET
    private var marginBottom = StyleUtils.NOT_SET
    private var marginLeft = StyleUtils.NOT_SET
    private var marginRight = StyleUtils.NOT_SET
    private var marginTop = StyleUtils.NOT_SET

    internal abstract fun ignoreLayoutWidthAndHeight(): Boolean

    override fun attributes(): IntArray? {
        return if (loaded) null else R.styleable.LayoutParams
    }

    override fun processAttribute(view: View, a: TypedArray, index: Int) {
        if (index == R.styleable.LayoutParams_android_layout_width && !ignoreLayoutWidthAndHeight()) {
            width = a.getLayoutDimension(index, 0)
        } else if (index == R.styleable.LayoutParams_android_layout_height && !ignoreLayoutWidthAndHeight()) {
            height = a.getLayoutDimension(index, 0)
        } else if (index == R.styleable.LayoutParams_android_layout_margin) {
            margin = a.getDimensionPixelSize(index, 0)
        } else if (index == R.styleable.LayoutParams_android_layout_marginBottom) {
            marginBottom = a.getDimensionPixelSize(index, 0)
        } else if (index == R.styleable.LayoutParams_android_layout_marginLeft) {
            marginLeft = a.getDimensionPixelSize(index, 0)
        } else if (index == R.styleable.LayoutParams_android_layout_marginRight) {
            marginRight = a.getDimensionPixelSize(index, 0)
        } else if (index == R.styleable.LayoutParams_android_layout_marginTop) {
            marginTop = a.getDimensionPixelSize(index, 0)
        }
    }

    override fun afterProcessAttributes(view: View) {
        loaded = true

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

    companion object {

        /**
         * [LayoutParamsStyle] doesn't support styling from an [android.util.AttributeSet],
         * the assumption being that the only case where an [android.util.AttributeSet] would be
         * used is after a [View] has been inflated from XML, hence the style attributes
         * supported by this class would already have been applied.
         */
        fun from(attrSet: AttributeSet, @StyleRes styleRes: Int, config: Style.Config?): LayoutParamsStyle {
            // AttributeSet intentionally set to null
            return AutoValue_LayoutParamsStyle(null, styleRes, null, config != null && config.contains(Option.IgnoreLayoutWidthAndHeight))
        }
    }
}
