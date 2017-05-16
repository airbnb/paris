package com.airbnb.paris

import android.animation.AnimatorInflater
import android.content.res.TypedArray
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.support.annotation.StyleRes
import android.util.AttributeSet
import android.view.View

import com.google.auto.value.AutoValue

@AutoValue
abstract class ViewStyle : BaseStyle<View>() {

    override fun parents(): List<Class<out Style<in View>>> {
        return listOf(LayoutParamsStyle::class.java)
    }

    override fun attributes(): IntArray? {
        return R.styleable.View
    }

    override fun processAttribute(view: View, a: TypedArray, index: Int) {
        if (index == R.styleable.View_android_background) {
            view.background = StyleUtils.getDrawable(view.context, a, index)
        } else if (index == R.styleable.View_android_minWidth) {
            view.minimumWidth = a.getDimensionPixelSize(index, -1)
        } else if (index == R.styleable.View_android_padding) {
            StyleUtils.setPadding(view, a.getDimensionPixelSize(index, -1))
        } else if (index == R.styleable.View_android_paddingBottom) {
            StyleUtils.setPaddingBottom(view, a.getDimensionPixelSize(index, -1))
        } else if (index == R.styleable.View_android_paddingLeft) {
            StyleUtils.setPaddingLeft(view, a.getDimensionPixelSize(index, -1))
        } else if (index == R.styleable.View_android_paddingRight) {
            StyleUtils.setPaddingRight(view, a.getDimensionPixelSize(index, -1))
        } else if (index == R.styleable.View_android_paddingTop) {
            StyleUtils.setPaddingTop(view, a.getDimensionPixelSize(index, -1))
        } else if (index == R.styleable.View_android_stateListAnimator) {
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                val resourceId = StyleUtils.getResourceId(a, index, 0)
                view.stateListAnimator = if (resourceId != 0)
                    AnimatorInflater.loadStateListAnimator(view.context, resourceId)
                else
                    null
            }
        }
    }

    companion object {

        /**
         * [ViewStyle] doesn't support styling from an [android.util.AttributeSet],
         * the assumption being that the only case where an [android.util.AttributeSet] would be
         * used is after a [View] has been inflated from XML, hence the style attributes
         * supported by this class would already have been applied.
         */
        fun from(attrSet: AttributeSet, @StyleRes styleRes: Int, config: Style.Config?): ViewStyle {
            // AttributeSet intentionally set to null
            return AutoValue_ViewStyle(null, styleRes, config)
        }
    }
}
