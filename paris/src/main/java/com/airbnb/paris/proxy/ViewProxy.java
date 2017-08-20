package com.airbnb.paris.proxy;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AnyRes;
import android.support.annotation.Px;
import android.view.View;

import com.airbnb.paris.LayoutParamsStyleApplier;
import com.airbnb.paris.R2;
import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.utils.ViewExtensionsKt;

@Styleable(value = "Paris_View", dependencies = LayoutParamsStyleApplier.class)
public class ViewProxy extends BaseProxy<ViewProxy, View> {

    /**
     * This replicates what happens privately within {@link View}
     */
    private static final int[] VISIBILITY_FLAGS = new int[]{ View.VISIBLE, View.INVISIBLE, View.GONE };

    public ViewProxy(View view) {
        super(view);
    }

    @Attr(R2.styleable.Paris_View_android_background)
    public void setBackground(Drawable drawable) {
        getView().setBackground(drawable);
    }

    @Attr(R2.styleable.Paris_View_android_minWidth)
    public void setMinWidth(@Px Integer minWidth) {
        getView().setMinimumWidth(minWidth);
    }

    @Attr(R2.styleable.Paris_View_android_padding)
    public void setPadding(@Px Integer padding) {
        getView().setPadding(padding, padding, padding, padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingBottom)
    public void setPaddingBottom(@Px Integer padding) {
        ViewExtensionsKt.setPaddingBottom(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingLeft)
    public void setPaddingLeft(@Px Integer padding) {
        ViewExtensionsKt.setPaddingLeft(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingRight)
    public void setPaddingRight(@Px Integer padding) {
        ViewExtensionsKt.setPaddingRight(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingTop)
    public void setPaddingTop(@Px Integer padding) {
        ViewExtensionsKt.setPaddingTop(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_stateListAnimator)
    public void setStateListAnimator(@AnyRes int animatorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StateListAnimator animator;
            if (animatorRes != 0) {
                animator = AnimatorInflater.loadStateListAnimator(getView().getContext(), animatorRes);
            } else {
                animator = null;
            }
            getView().setStateListAnimator(animator);
        }
    }

    @Attr(R2.styleable.Paris_View_android_visibility)
    public void setVisibility(int visibility) {
        getView().setVisibility(VISIBILITY_FLAGS[visibility]);
    }
}
