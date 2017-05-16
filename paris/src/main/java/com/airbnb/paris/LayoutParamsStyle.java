package com.airbnb.paris;

import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LayoutParamsStyle extends BaseStyle<View> {

    public enum Option implements Style.Config.Option {
        IgnoreLayoutWidthAndHeight
    }

    private boolean loaded = false;

    private int width = StyleUtils.NOT_SET;
    private int height = StyleUtils.NOT_SET;
    private int margin = StyleUtils.NOT_SET;
    private int marginBottom = StyleUtils.NOT_SET;
    private int marginLeft = StyleUtils.NOT_SET;
    private int marginRight = StyleUtils.NOT_SET;
    private int marginTop = StyleUtils.NOT_SET;

    /**
     * {@link LayoutParamsStyle} doesn't support styling from an {@link android.util.AttributeSet},
     * the assumption being that the only case where an {@link android.util.AttributeSet} would be
     * used is after a {@link View} has been inflated from XML, hence the style attributes
     * supported by this class would already have been applied.
     */
    public static LayoutParamsStyle from(AttributeSet attrSet, @StyleRes int styleRes, @Nullable Config config) {
        // AttributeSet intentionally set to null
        return new AutoValue_LayoutParamsStyle(null, styleRes, null, config != null && config.contains(Option.IgnoreLayoutWidthAndHeight));
    }

    abstract boolean ignoreLayoutWidthAndHeight();

    @Override
    protected int[] attributes() {
        return loaded ? null : R.styleable.LayoutParams;
    }

    @Override
    protected void processAttribute(View view, TypedArray a, int index) {
        if (index == R.styleable.LayoutParams_android_layout_width && !ignoreLayoutWidthAndHeight()) {
            width = a.getLayoutDimension(index, 0);
        } else if (index == R.styleable.LayoutParams_android_layout_height && !ignoreLayoutWidthAndHeight()) {
            height = a.getLayoutDimension(index, 0);
        } else if (index == R.styleable.LayoutParams_android_layout_margin) {
            margin = a.getDimensionPixelSize(index, 0);
        } else if (index == R.styleable.LayoutParams_android_layout_marginBottom) {
            marginBottom = a.getDimensionPixelSize(index, 0);
        } else if (index == R.styleable.LayoutParams_android_layout_marginLeft) {
            marginLeft = a.getDimensionPixelSize(index, 0);
        } else if (index == R.styleable.LayoutParams_android_layout_marginRight) {
            marginRight = a.getDimensionPixelSize(index, 0);
        } else if (index == R.styleable.LayoutParams_android_layout_marginTop) {
            marginTop = a.getDimensionPixelSize(index, 0);
        }
    }

    @Override
    protected void afterProcessAttributes(View view) {
        loaded = true;

        if (width != StyleUtils.NOT_SET ^ height != StyleUtils.NOT_SET) {
            throw new IllegalArgumentException("Width and height must either both be set, or not be set at all. It can't be one and not the other.");
        }

        boolean isWidthHeightSet = width != StyleUtils.NOT_SET; // Height follows given the XOR condition above
        boolean isMarginSet = StyleUtils.isAnySet(margin, marginBottom, marginLeft, marginRight, marginTop);

        if (isWidthHeightSet) {
            LayoutParams params = view.getLayoutParams();
            if (params == null) {
                params = isMarginSet ? new MarginLayoutParams(width, height) : new LayoutParams(width, height);
            } else {
                params.width = width;
                params.height = height;
            }
            view.setLayoutParams(params);
        }

        if (isMarginSet) {
            MarginLayoutParams marginParams = (MarginLayoutParams) view.getLayoutParams();
            if (margin != StyleUtils.NOT_SET) {
                marginParams.setMargins(margin, margin, margin, margin);
            }
            marginParams.bottomMargin = StyleUtils.ifSetElse(marginBottom, marginParams.bottomMargin);
            marginParams.leftMargin = StyleUtils.ifSetElse(marginLeft, marginParams.leftMargin);
            marginParams.rightMargin = StyleUtils.ifSetElse(marginRight, marginParams.rightMargin);
            marginParams.topMargin = StyleUtils.ifSetElse(marginTop, marginParams.topMargin);
            view.setLayoutParams(marginParams);
        }
    }
}
