package com.airbnb.paris;

import android.animation.AnimatorInflater;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;

@AutoValue
public abstract class ViewStyle extends BaseStyle<View> {

    /**
     * {@link ViewStyle} doesn't support styling from an {@link android.util.AttributeSet},
     * the assumption being that the only case where an {@link android.util.AttributeSet} would be
     * used is after a {@link View} has been inflated from XML, hence the style attributes
     * supported by this class would already have been applied.
     */
    public static ViewStyle from(AttributeSet attrSet, @StyleRes int styleRes, @Nullable Config config) {
        // AttributeSet intentionally set to null
        return new AutoValue_ViewStyle(null, styleRes, config);
    }

    @Override
    protected List<Class<? extends Style<? super View>>> parents() {
        List<Class<? extends Style<? super View>>> list = new ArrayList<>();
        list.add(LayoutParamsStyle.class);
        return list;
    }

    @Override
    protected int[] attributes() {
        return R.styleable.View;
    }

    @Override
    protected void processAttribute(View view, TypedArray a, int index) {
        if (index == R.styleable.View_android_background) {
            view.setBackground(StyleUtils.getDrawable(view.getContext(), a, index));
        } else if (index == R.styleable.View_android_minWidth) {
            view.setMinimumWidth(a.getDimensionPixelSize(index, -1));
        } else if (index == R.styleable.View_android_padding) {
            StyleUtils.setPadding(view, a.getDimensionPixelSize(index, -1));
        } else if (index == R.styleable.View_android_paddingBottom) {
            StyleUtils.setPaddingBottom(view, a.getDimensionPixelSize(index, -1));
        } else if (index == R.styleable.View_android_paddingLeft) {
            StyleUtils.setPaddingLeft(view, a.getDimensionPixelSize(index, -1));
        } else if (index == R.styleable.View_android_paddingRight) {
            StyleUtils.setPaddingRight(view, a.getDimensionPixelSize(index, -1));
        } else if (index == R.styleable.View_android_paddingTop) {
            StyleUtils.setPaddingTop(view, a.getDimensionPixelSize(index, -1));
        } else if (index == R.styleable.View_android_stateListAnimator) {
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                int resourceId = StyleUtils.getResourceId(a, index, 0);
                view.setStateListAnimator(resourceId != 0 ?
                        AnimatorInflater.loadStateListAnimator(view.getContext(), resourceId) :
                        null);
            }
        }
    }
}
