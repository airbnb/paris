package com.airbnb.paris.proxies;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AnyRes;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.airbnb.paris.R2;
import com.airbnb.paris.annotations.AfterStyle;
import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.BeforeStyle;
import com.airbnb.paris.annotations.LayoutDimension;
import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.styles.Style;
import com.airbnb.paris.utils.ViewExtensionsKt;

/**
 * The order of the methods in a styleable class dictates the order in which attributes are applied. This class relies on this fact to enforces the
 * precedence of paddings. It's unorthodox but it simplifies the logic quite a bit. DO NOT RELY ON THIS UNDOCUMENTED FEATURE IF YOUR PROJECT IMPORTS
 * THIS LIBRARY.
 */
@Styleable(value = "Paris_View")
public class ViewProxy extends BaseProxy<ViewProxy, View> {

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

    private static final SparseIntArray VISIBILITY_MAP = new SparseIntArray();

    static {
        // Visibility values passed to setVisibility are assumed to be one of View.VISIBLE (0),
        // INVISIBLE (4) and GONE (8) if passed to a style builder as a "direct" value, or an int
        // between 0 and 2 if passed as an enum resource (either through a builder or XML style).
        // This maps all the possible inputs to the correct visiblity
        VISIBILITY_MAP.put(View.VISIBLE, View.VISIBLE);
        VISIBILITY_MAP.put(View.INVISIBLE, View.INVISIBLE);
        VISIBILITY_MAP.put(View.GONE, View.GONE);
        VISIBILITY_MAP.put(1, View.INVISIBLE);
        VISIBILITY_MAP.put(2, View.GONE);
    }

    private boolean ignoreLayoutWidthAndHeight;
    private int width;
    private int height;
    private int margin;
    private int marginBottom;
    private int marginEnd;
    private int marginLeft;
    private int marginRight;
    private int marginStart;
    private int marginTop;

    public ViewProxy(View view) {
        super(view);
    }

    @BeforeStyle
    public void beforeStyle(Style style) {
        ignoreLayoutWidthAndHeight = false;
        width = NOT_SET;
        height = NOT_SET;
        margin = NOT_SET;
        marginBottom = NOT_SET;
        marginEnd = NOT_SET;
        marginLeft = NOT_SET;
        marginRight = NOT_SET;
        marginStart = NOT_SET;
        marginTop = NOT_SET;
    }

    @AfterStyle
    public void afterStyle(Style style) {
        boolean isMarginSet = isAnySet(margin, marginBottom, marginEnd, marginLeft, marginRight, marginStart, marginTop);

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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    marginParams.setLayoutDirection(getView().getLayoutDirection());
                }
            }
            if (margin != NOT_SET) {
                marginParams.setMargins(margin, margin, margin, margin);
            } else {
                marginParams.bottomMargin = ifSetElse(marginBottom, marginParams.bottomMargin);
                marginParams.leftMargin = ifSetElse(marginLeft, marginParams.leftMargin);
                marginParams.rightMargin = ifSetElse(marginRight, marginParams.rightMargin);
                marginParams.topMargin = ifSetElse(marginTop, marginParams.topMargin);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    // Note: setting negatives marginEnd or marginStart doesn't work (the view resets them to 0)
                    if (marginEnd != NOT_SET) marginParams.setMarginEnd(marginEnd);
                    if (marginStart != NOT_SET) marginParams.setMarginStart(marginStart);
                }
            }
            getView().setLayoutParams(marginParams);
        }
    }

    @Attr(R2.styleable.Paris_View_android_layout_width)
    public void setLayoutWidth(@LayoutDimension int width) {
        this.width = width;
    }

    @Attr(R2.styleable.Paris_View_android_layout_height)
    public void setLayoutHeight(@LayoutDimension int height) {
        this.height = height;
    }

    @Attr(R2.styleable.Paris_View_android_layout_gravity)
    public void setLayoutGravity(int gravity) {
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

    @Attr(R2.styleable.Paris_View_android_layout_marginBottom)
    public void setLayoutMarginBottom(@Px int marginBottom) {
        this.marginBottom = marginBottom;
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginLeft)
    public void setLayoutMarginLeft(@Px int marginLeft) {
        this.marginLeft = marginLeft;
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginRight)
    public void setLayoutMarginRight(@Px int marginRight) {
        this.marginRight = marginRight;
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginTop)
    public void setLayoutMarginTop(@Px int marginTop) {
        this.marginTop = marginTop;
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginEnd)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setLayoutMarginEnd(@Px int marginEnd) {
        this.marginEnd = marginEnd;
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginStart)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setLayoutMarginStart(@Px int marginStart) {
        this.marginStart = marginStart;
    }

    @Attr(R2.styleable.Paris_View_android_layout_margin)
    public void setLayoutMargin(@Px int margin) {
        this.margin = margin;
    }

    @Attr(R2.styleable.Paris_View_android_alpha)
    public void setAlpha(float alpha) {
        getView().setAlpha(alpha);
    }

    @Attr(R2.styleable.Paris_View_android_background)
    public void setBackground(@Nullable Drawable drawable) {
        getView().setBackground(drawable);
    }

    @Attr(R2.styleable.Paris_View_android_backgroundTint)
    public void setBackgroundTint(@ColorRes int colorRes) {
        ViewCompat.setBackgroundTintList(
                getView(),
                ContextCompat.getColorStateList(getView().getContext(), colorRes)
        );
    }

    @Attr(R2.styleable.Paris_View_android_backgroundTintMode)
    public void setBackgroundTintMode(int tintMode) {
        ViewCompat.setBackgroundTintMode(getView(), parseTintMode(tintMode, null));
    }

    private PorterDuff.Mode parseTintMode(int value, PorterDuff.Mode defaultMode) {
        switch (value) {
            case 3: return PorterDuff.Mode.SRC_OVER;
            case 5: return PorterDuff.Mode.SRC_IN;
            case 9: return PorterDuff.Mode.SRC_ATOP;
            case 14: return PorterDuff.Mode.MULTIPLY;
            case 15: return PorterDuff.Mode.SCREEN;
            case 16: return PorterDuff.Mode.ADD;
            default: return defaultMode;
        }
    }


    @Attr(R2.styleable.Paris_View_android_contentDescription)
    public void setContentDescription(@Nullable CharSequence contentDescription) {
        getView().setContentDescription(contentDescription);
    }

    @Attr(R2.styleable.Paris_View_android_elevation)
    public void setElevation(@Px int elevation) {
        ViewCompat.setElevation(getView(), elevation);
    }

    @Attr(R2.styleable.Paris_View_android_foreground)
    public void setForeground(@Nullable Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getView().setForeground(drawable);
        }
    }

    @Attr(R2.styleable.Paris_View_android_minHeight)
    public void setMinHeight(@Px int minHeight) {
        getView().setMinimumHeight(minHeight);
    }

    @Attr(R2.styleable.Paris_View_android_minWidth)
    public void setMinWidth(@Px int minWidth) {
        getView().setMinimumWidth(minWidth);
    }

    @Attr(R2.styleable.Paris_View_android_paddingBottom)
    public void setPaddingBottom(@Px int padding) {
        ViewExtensionsKt.setPaddingBottom(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingLeft)
    public void setPaddingLeft(@Px int padding) {
        ViewExtensionsKt.setPaddingLeft(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingRight)
    public void setPaddingRight(@Px int padding) {
        ViewExtensionsKt.setPaddingRight(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingTop)
    public void setPaddingTop(@Px int padding) {
        ViewExtensionsKt.setPaddingTop(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingHorizontal)
    public void setPaddingHorizontal(@Px int padding) {
        ViewExtensionsKt.setPaddingHorizontal(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingVertical)
    public void setPaddingVertical(@Px int padding) {
        ViewExtensionsKt.setPaddingVertical(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_padding)
    public void setPadding(@Px int padding) {
        getView().setPadding(padding, padding, padding, padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingEnd)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setPaddingEnd(@Px int padding) {
        ViewExtensionsKt.setPaddingEnd(getView(), padding);
    }

    @Attr(R2.styleable.Paris_View_android_paddingStart)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setPaddingStart(@Px int padding) {
        ViewExtensionsKt.setPaddingStart(getView(), padding);
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
        getView().setVisibility(VISIBILITY_MAP.get(visibility));
    }

    @Attr(R2.styleable.Paris_View_ignoreLayoutWidthAndHeight)
    public void setIgnoreLayoutWidthAndHeight(boolean ignore) {
        ignoreLayoutWidthAndHeight = ignore;
    }
}
