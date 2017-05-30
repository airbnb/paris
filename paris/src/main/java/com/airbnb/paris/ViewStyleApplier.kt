package com.airbnb.paris

import android.animation.AnimatorInflater
import android.os.Build
import android.view.View

class ViewStyleApplier(view: View) : StyleApplier<ViewStyleApplier, View>(view) {

    override fun attributes(): IntArray {
        return R.styleable.Paris_View
    }

//    fun background(@DrawableRes drawableRes: Int): ViewStyleApplier {
//        apply(Style.builder()
//                .put(R.styleable.View_android_background, drawableRes)
//                .build())
//        return this
//    }
//
//    fun background(drawable: Drawable): ViewStyleApplier {
//        // TODO
//        return this
//    }

    override fun applyDependencies(style: Style) {
        LayoutParamsStyleApplier(view).apply(style)
    }

    override fun processAttributes(style: Style, a: TypedArrayWrapper) {
        if (a.hasValue(R.styleable.Paris_View_android_background)) {
            view.background = StyleUtils.getDrawable(view.context, a, R.styleable.Paris_View_android_background)
        }
        if (a.hasValue(R.styleable.Paris_View_android_minWidth)) {
            view.minimumWidth = a.getDimensionPixelSize(R.styleable.Paris_View_android_minWidth, -1)
        }
        if (a.hasValue(R.styleable.Paris_View_android_padding)) {
            view.setPadding(a.getDimensionPixelSize(R.styleable.Paris_View_android_padding, -1))
        }
        if (a.hasValue(R.styleable.Paris_View_android_paddingBottom)) {
            view.setPaddingBottom(a.getDimensionPixelSize(R.styleable.Paris_View_android_paddingBottom, -1))
        }
        if (a.hasValue(R.styleable.Paris_View_android_paddingLeft)) {
            view.setPaddingLeft(a.getDimensionPixelSize(R.styleable.Paris_View_android_paddingLeft, -1))
        }
        if (a.hasValue(R.styleable.Paris_View_android_paddingRight)) {
            view.setPaddingRight(a.getDimensionPixelSize(R.styleable.Paris_View_android_paddingRight, -1))
        }
        if (a.hasValue(R.styleable.Paris_View_android_paddingTop)) {
            view.setPaddingTop(a.getDimensionPixelSize(R.styleable.Paris_View_android_paddingTop, -1))
        }
        if (a.hasValue(R.styleable.Paris_View_android_stateListAnimator)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val resourceId = a.getResourceId(R.styleable.Paris_View_android_stateListAnimator, 0)
                view.stateListAnimator = if (resourceId != 0)
                    AnimatorInflater.loadStateListAnimator(view.context, resourceId)
                else
                    null
            }
        }
    }
}