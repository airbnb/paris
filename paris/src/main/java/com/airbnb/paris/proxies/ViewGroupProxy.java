package com.airbnb.paris.proxies;

import android.animation.LayoutTransition;
import android.view.ViewGroup;

import com.airbnb.paris.R2;
import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Styleable;

@Styleable(value = "Paris_ViewGroup")
public class ViewGroupProxy extends BaseProxy<ViewGroupProxy, ViewGroup> {

    ViewGroupProxy(ViewGroup view) {
        super(view);
    }

    @Attr(R2.styleable.Paris_ViewGroup_android_animateLayoutChanges)
    void setAnimateLayoutChanges(boolean animateLayoutChanges) {
        getView().setLayoutTransition(animateLayoutChanges ? new LayoutTransition() : null);
    }

    @Attr(R2.styleable.Paris_ViewGroup_android_clipChildren)
    void setClipChildren(boolean clipChildren) {
        getView().setClipChildren(clipChildren);
    }

    @Attr(R2.styleable.Paris_ViewGroup_android_clipToPadding)
    void setClipToPadding(boolean clipToPadding) {
        getView().setClipToPadding(clipToPadding);
    }
}
