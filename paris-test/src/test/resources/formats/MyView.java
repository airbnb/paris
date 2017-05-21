package com.airbnb.paris.test;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Format;
import com.airbnb.paris.annotations.Styleable;

// TODO  Test default values
@Styleable("Formats")
public class MyView extends View {

    @Attr(R2.styleable.Formats_formatBoolean) boolean formatBoolean;
    @Attr(R2.styleable.Formats_formatColor) @ColorInt int formatColor;
    @Attr(R2.styleable.Formats_formatDimension) @Px int formatDimension;
    @Attr(R2.styleable.Formats_formatEnum) int formatEnum;
    @Attr(R2.styleable.Formats_formatFlag) int formatFlag;
    @Attr(R2.styleable.Formats_formatFloat) float formatFloat;
    @Attr(value = R2.styleable.Formats_formatFraction, format = Format.FRACTION) float formatFraction;
    @Attr(R2.styleable.Formats_formatInteger) int formatInteger;
    @Attr(R2.styleable.Formats_formatReference) CharSequence[] formatReference_CharSequenceArray;
    @Attr(R2.styleable.Formats_formatReference) ColorStateList formatReference_ColorStateList;
    @Attr(R2.styleable.Formats_formatReference) Drawable formatReference_Drawable;
    @Attr(R2.styleable.Formats_formatReference) int formatReference_int;
    @Attr(R2.styleable.Formats_formatString) CharSequence formatString_CharSequence;
    @Attr(R2.styleable.Formats_formatString) String formatString_String;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}