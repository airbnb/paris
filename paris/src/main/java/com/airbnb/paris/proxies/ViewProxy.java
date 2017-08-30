package com.airbnb.paris.proxies;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AnyRes;
import android.support.annotation.Px;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.airbnb.paris.R2;
import com.airbnb.paris.Style;
import com.airbnb.paris.annotations.AfterStyle;
import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.BeforeStyle;
import com.airbnb.paris.annotations.LayoutDimension;
import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.utils.ViewExtensionsKt;

@Styleable(value = "Paris_View")
class ViewProxy extends BaseProxy<ViewProxy, View> {

    private static final int NOT_SET = -10;

    private static int ifSetElse(int value, int ifNotSet) {
            return value != NOT_SET ? value : ifNotSet;
    }

    private static boolean isAnySet(int... values) {
        for (int value : values) {
            if (value != NOT_SET) {
                return true;
            }
        }
        return false;
    }

    /**
     * This replicates what happens privately within {@link View}
     */
    private static final int[] VISIBILITY_FLAGS = new int[]{ View.VISIBLE, View.INVISIBLE, View.GONE };

    private boolean ignoreLayoutWidthAndHeight;
    private int width;
    private int height;
    private int margin;
    private int marginBottom;
    private int marginLeft;
    private int marginRight;
    private int marginTop;

    ViewProxy(View view) {
        super(view);
    }

    @BeforeStyle
    void beforeStyle(Style style) {
        ignoreLayoutWidthAndHeight = false;
        width = NOT_SET;
        height = NOT_SET;
        margin = NOT_SET;
        marginBottom = NOT_SET;
        marginLeft = NOT_SET;
        marginRight = NOT_SET;
        marginTop = NOT_SET;
    }

    @AfterStyle
    void afterStyle(Style style) {
        boolean isMarginSet = isAnySet(margin, marginBottom, marginLeft, marginRight, marginTop);

        if (!ignoreLayoutWidthAndHeight) {
            if ((width != NOT_SET) ^ (height != NOT_SET)) {
                throw new IllegalArgumentException("Width and height must either both be set, or not be set at all. It can't be one and not the other.");
            }

            boolean isWidthHeightSet = width != NOT_SET; // Height follows given the XOR condition above

            if (isWidthHeightSet) {
                LayoutParams params = getView().getLayoutParams();
                if (params == null) {
                    params = isMarginSet ? new MarginLayoutParams(width, height) : new LayoutParams(width, height);
                } else {
                    params.width = width;
                    params.height = height;
                }
                getView().setLayoutParams(params);
            }
        }

        if (isMarginSet) {
            MarginLayoutParams marginParams;
            if (getView().getLayoutParams() != null) {
                marginParams = (MarginLayoutParams) getView().getLayoutParams();
            } else {
                //noinspection ResourceType
                marginParams = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }
            if (margin != NOT_SET) {
                marginParams.setMargins(margin, margin, margin, margin);
            }
            marginParams.bottomMargin = ifSetElse(marginBottom, marginParams.bottomMargin);
            marginParams.leftMargin = ifSetElse(marginLeft, marginParams.leftMargin);
            marginParams.rightMargin = ifSetElse(marginRight, marginParams.rightMargin);
            marginParams.topMargin = ifSetElse(marginTop, marginParams.topMargin);
            getView().setLayoutParams(marginParams);
        }
    }

    @Attr(R2.styleable.Paris_View_android_layout_width)
    void setLayoutWidth(@LayoutDimension int width) {
        this.width = width;
    }

    @Attr(R2.styleable.Paris_View_android_layout_height)
    void setLayoutHeight(@LayoutDimension int height) {
        this.height = height;
    }

    @Attr(R2.styleable.Paris_View_android_layout_gravity)
    void setLayoutGravity(int gravity) {
        LayoutParams params = getView().getLayoutParams();
        if (params != null) {
            if (params instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) params).gravity = gravity;
            } else if (params instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) params).gravity = gravity;
            }
            getView().setLayoutParams(params);
        }
    }

    @Attr(R2.styleable.Paris_View_android_layout_margin)
    void setLayoutMargin(@Px int margin) {
        this.margin = margin;
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginBottom)
    void setLayoutMarginBottom(@Px int marginBottom) {
        this.marginBottom = marginBottom;
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginLeft)
    void setLayoutMarginLeft(@Px int marginLeft) {
        this.marginLeft = marginLeft;
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginRight)
    void setLayoutMarginRight(@Px int marginRight) {
        this.marginRight = marginRight;
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginTop)
    void setLayoutMarginTop(@Px int marginTop) {
        this.marginTop = marginTop;
    }

    @Attr(R2.styleable.Paris_View_android_background)
    void setBackground(Drawable drawable) {
        getView().setBackground(drawable);
    }

    @Attr(R2.styleable.Paris_View_android_minWidth)
    void setMinWidth(@Px int minWidth) {
        getView().setMinimumWidth(minWidth);
    }

    @Attr(R2.styleable.Paris_View_android_padding)
    void setPadding(@Px int padding) {
        getView().setPadding(padding, padding, padding, padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingBottom)
    void setPaddingBottom(@Px int padding) {
        ViewExtensionsKt.setPaddingBottom(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingLeft)
    void setPaddingLeft(@Px int padding) {
        ViewExtensionsKt.setPaddingLeft(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingRight)
    void setPaddingRight(@Px int padding) {
        ViewExtensionsKt.setPaddingRight(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingTop)
    void setPaddingTop(@Px int padding) {
        ViewExtensionsKt.setPaddingTop(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_stateListAnimator)
    void setStateListAnimator(@AnyRes int animatorRes) {
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
    void setVisibility(int visibility) {
        getView().setVisibility(VISIBILITY_FLAGS[visibility]);
    }

    @Attr(R2.styleable.Paris_View_ignoreLayoutWidthAndHeight)
    void setVisibility(boolean ignore) {
        ignoreLayoutWidthAndHeight = ignore;
    }
}
