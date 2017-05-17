package com.airbnb.paris;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;

import java.util.Collections;
import java.util.List;

/**
 * A helper class for styles to:
 * <ul>
 * <li>Explicitely declare parents to be applied automatically</li>
 * <li>Read attribute values from either a style resource or an {@link android.util.AttributeSet}</li>
 * </ul>
 */
public abstract class BaseStyle<T extends View> implements Style<T> {

    @Nullable
    public abstract AttributeSet attrSet();

    @StyleRes
    public abstract int styleRes();

    @Nullable
    public abstract Config config();

    /**
     * Instantiates and applies the style classes returned here right before this style is applied
     * itself.
     *
     * Note: when using {@link Styles#change(View)} to apply styles the entire hierarchy of View
     * styles is automatically applied. For example changing a {@link android.widget.TextView}
     * will automatically apply both {@link TextViewStyle} and {@link ViewStyle}, because
     * {@link View} is a parent of {@link android.widget.TextView}. As a result it is recommended
     * not to return style classes here that will automatically be detected following the
     * {@link View} hierarchy.
     *
     * @return  Parent styles to be applied before this one is. Each style is considered in-order,
     *          with the first taking precedence over the following ones.
     */
    protected List<Class<? extends Style<? super T>>> parents() {
        return Collections.emptyList();
    }

    @Nullable
    protected int[] attributes() {
        return null;
    }

    /**
     * Called before {@link #processAttribute(View, TypedArray, int)} would be called, regardless of whether it is actually called or not.
     */
    protected void beforeProcessAttributes(T view) {
        // Default implementation does nothing
    }

    /**
     * Iterates over the {@link AttributeSet} and/or {@link StyleRes}'s {@link TypedArray} and
     * calls {@link #processAttribute(View, TypedArray, int)} for each attribute.
     */
    protected void processAttributes(T view) {
        Context context = view.getContext();

        boolean hasStyleData = attrSet() != null || styleRes() != 0;
        @SuppressWarnings("ConstantConditions")
        boolean hasUnstyledAttributes = attributes() != null && attributes().length > 0;
        if (hasStyleData && hasUnstyledAttributes) {
            TypedArray typedArray = StyleUtils.obtainStyledAttributes(context, attrSet(), styleRes(), attributes());
            if (typedArray != null) {
                for (int i = 0, N = typedArray.getIndexCount(); i < N; i++) {
                    processAttribute(view, typedArray, typedArray.getIndex(i));
                }
                typedArray.recycle();
            }
        }
    }

    protected void processAttribute(T view, TypedArray typedArray, int index) {
        // Default implementation does nothing
    }

    /**
     * Called after {@link #processAttribute(View, TypedArray, int)} would be called, regardless of whether it is actually called or not.
     */
    protected void afterProcessAttributes(T view) {
        // Default implementation does nothing
    }

    public final void applyTo(T view) {
        // This applies the explicitly declared parents
        for (Class<? extends Style<? super T>> parentClass : parents()) {
            StyleUtils.create(parentClass, attrSet(), styleRes(), config()).applyTo(view);
        }

        beforeProcessAttributes(view);
        processAttributes(view);
        afterProcessAttributes(view);
    }
}
