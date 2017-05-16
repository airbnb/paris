package com.airbnb.paris;

import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class TextViewStyle extends BaseStyle<TextView> {

    /**
     * {@link ViewStyle} doesn't support styling from an {@link android.util.AttributeSet},
     * the assumption being that the only case where an {@link android.util.AttributeSet} would be
     * used is after a {@link View} has been inflated from XML, hence the style attributes
     * supported by this class would already have been applied.
     */
    public static TextViewStyle from(AttributeSet attrSet, @StyleRes int styleRes, @Nullable Config config) {
        // AttributeSet intentionally set to null
        return new AutoValue_TextViewStyle(null, styleRes, config);
    }

    @Override
    protected int[] attributes() {
        return R.styleable.TextView;
    }

    @Override
    protected void processAttribute(TextView view, TypedArray a, int index) {
        if (index == R.styleable.TextView_android_ellipsize) {
            setEllipsize(view, a.getInt(index, -1));
        } else if (index == R.styleable.TextView_android_gravity) {
            view.setGravity(a.getInt(index, -1));
        } else if (index == R.styleable.TextView_android_letterSpacing) {
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                view.setLetterSpacing(a.getFloat(index, 0));
            }
        } else if (index == R.styleable.TextView_android_lineSpacingExtra) {
            view.setLineSpacing(a.getDimensionPixelSize(index, 0), view.getLineSpacingMultiplier());
        } else if (index == R.styleable.TextView_android_lineSpacingMultiplier) {
            view.setLineSpacing(view.getLineSpacingExtra(), a.getFloat(index, 1));
        } else if (index == R.styleable.TextView_android_maxLines) {
            view.setMaxLines(a.getInt(index, -1));
        } else if (index == R.styleable.TextView_android_minWidth) {
            view.setMinWidth(a.getDimensionPixelSize(index, -1));
        } else if (index == R.styleable.TextView_android_textAllCaps) {
            view.setAllCaps(a.getBoolean(index, false));
        } else if (index == R.styleable.TextView_android_textColor) {
            view.setTextColor(a.getColorStateList(index));
        } else if (index == R.styleable.TextView_android_textSize) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimensionPixelSize(index, 0));
        }
    }

    private void setEllipsize(TextView view, int value) {
        switch (value) {
            case 1:
                view.setEllipsize(TextUtils.TruncateAt.START);
                break;
            case 2:
                view.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                break;
            case 3:
                view.setEllipsize(TextUtils.TruncateAt.END);
                break;
            case 4:
                view.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                break;
        }
    }
}
