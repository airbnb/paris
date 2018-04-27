package com.airbnb.paris.proxies

import android.animation.LayoutTransition
import android.view.ViewGroup

import com.airbnb.paris.R2
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable

@Styleable("Paris_ViewGroup")
class ViewGroupProxy(view: ViewGroup) : BaseProxy<ViewGroupProxy, ViewGroup>(view) {

    @Attr(R2.styleable.Paris_ViewGroup_android_animateLayoutChanges)
    fun setAnimateLayoutChanges(animateLayoutChanges: Boolean) {
        view.layoutTransition = if (animateLayoutChanges) LayoutTransition() else null
    }

    @Attr(R2.styleable.Paris_ViewGroup_android_clipChildren)
    fun setClipChildren(clipChildren: Boolean) {
        view.clipChildren = clipChildren
    }

    @Attr(R2.styleable.Paris_ViewGroup_android_clipToPadding)
    fun setClipToPadding(clipToPadding: Boolean) {
        view.clipToPadding = clipToPadding
    }
}
