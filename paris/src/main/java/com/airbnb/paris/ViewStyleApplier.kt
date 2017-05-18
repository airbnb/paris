package com.airbnb.paris

import android.animation.AnimatorInflater
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.DrawableRes
import android.view.View

open class ViewStyleApplier(view: View) : StyleApplier<View>(view) {

    override fun attributes(): IntArray {
        return R.styleable.View
    }

    fun background(@DrawableRes drawableRes: Int): ViewStyleApplier {
        apply(Style.builder()
                .put(R.styleable.View_android_background, drawableRes)
                .build())
        return this
    }

    fun background(drawable: Drawable): ViewStyleApplier {
        // TODO
        return this
    }

    override fun beforeProcessAttributes(style: Style) {
        LayoutParamsStyleApplier(view).apply(style)
    }

    override fun processAttribute(style: Style, a: TypedArrayWrapper, index: Int) {
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val resourceId = StyleUtils.getResourceId(a, index, 0)
                view.stateListAnimator = if (resourceId != 0)
                    AnimatorInflater.loadStateListAnimator(view.context, resourceId)
                else
                    null
            }
        }
    }
}