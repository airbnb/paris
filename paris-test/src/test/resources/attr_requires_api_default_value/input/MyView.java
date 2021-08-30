package com.airbnb.paris.test;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.AnyRes;
import androidx.annotation.BoolRes;
import androidx.annotation.ColorInt;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Fraction;
import com.airbnb.paris.annotations.LayoutDimension;
import com.airbnb.paris.annotations.Styleable;

@Styleable("Formats")
public class MyView extends View {

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Attr(value = R2.styleable.Formats_formatBoolean, defaultValue = R2.bool.format_boolean)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void formatBoolean(boolean value) {}
}
