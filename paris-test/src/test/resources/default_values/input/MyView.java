package com.airbnb.paris.test;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.AnyRes;
import androidx.annotation.ColorInt;
import androidx.annotation.Px;
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
    public void formatBoolean(boolean value) {
    }

    @Attr(value = R2.styleable.Formats_formatColor, defaultValue = R2.color.format_color)
    public void formatColor(@ColorInt int value) {
    }

    @Attr(value = R2.styleable.Formats_formatDimension, defaultValue = R2.dimen.format_dimension)
    public void formatDimension_px(@Px int value) {
    }

    @Attr(value = R2.styleable.Formats_formatDimension, defaultValue = R2.dimen.format_dimension)
    public void formatDimension_LayoutDimension(@LayoutDimension int value) {
    }

    @Attr(value = R2.styleable.Formats_formatEnum, defaultValue = R2.integer.format_enum)
    public void formatEnum(int value) {
    }

    @Attr(value = R2.styleable.Formats_formatFlag, defaultValue = R2.integer.format_flag)
    public void formatFlag(int value) {
    }

    @Attr(value = R2.styleable.Formats_formatFloat, defaultValue = R2.dimen.format_float)
    public void formatFloat(float value) {
    }

    @Attr(value = R2.styleable.Formats_formatFraction, defaultValue = R2.fraction.format_fraction)
    public void formatFraction(@Fraction(base = 2, pbase = 3) float value) {
    }

    @Attr(value = R2.styleable.Formats_formatInteger, defaultValue = R2.integer.format_integer)
    public void formatInteger(int value) {
    }

    @Attr(value = R2.styleable.Formats_formatReference, defaultValue = R2.array.format_string_array)
    public void formatReference_CharSequenceArray(CharSequence[] value) {
    }

    // Arbitrary resource used as a default value
    @Attr(value = R2.styleable.Formats_formatReference, defaultValue = R2.bool.active)
    public void formatReference_res(@AnyRes int value) {
    }

    @Attr(value = R2.styleable.Formats_formatReference2, defaultValue = R2.color.format_color_state_list)
    public void formatReference_ColorStateList(ColorStateList value) {
    }

    @Attr(value = R2.styleable.Formats_formatReference3, defaultValue = R2.drawable.format_drawable)
    public void formatReference_Drawable(Drawable value) {
    }

    @Attr(value = R2.styleable.Formats_formatReference4, defaultValue = R2.font.format_font)
    public void formatReference_Font(Typeface value) {
    }

    @Attr(value = R2.styleable.Formats_formatString, defaultValue = R2.string.format_char_sequence)
    public void formatString_CharSequence(CharSequence value) {
    }

    @Attr(value = R2.styleable.Formats_formatString2, defaultValue = R2.string.format_string)
    public void formatString_String(String value) {
    }
}
