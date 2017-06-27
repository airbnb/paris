package com.airbnb.paris.test;

import android.content.res.Resources;
import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.TypedArrayWrapper;
import com.airbnb.paris.ViewStyleApplier;
import java.lang.Override;

public final class MyViewStyleApplier extends StyleApplier<MyViewStyleApplier, MyView> {
    public MyViewStyleApplier(MyView view) {
        super(view);
    }

    @Override
    protected int[] attributes() {
        return R.styleable.Formats;
    }

    @Override
    protected void processAttributes(Style style, TypedArrayWrapper a) {
        Resources res = getView().getContext().getResources();
        if (a.hasValue(R.styleable.Formats_formatBoolean)) {
            getView().formatBoolean(a.getBoolean(R.styleable.Formats_formatBoolean, false));
        }
        else {
            getView().formatBoolean(res.getBoolean(R.bool.format_boolean));
        }
        if (a.hasValue(R.styleable.Formats_formatColor)) {
            getView().formatColor(a.getColor(R.styleable.Formats_formatColor, -1));
        }
        else {
            getView().formatColor(res.getColor(R.color.format_color));
        }
        if (a.hasValue(R.styleable.Formats_formatDimension)) {
            getView().formatDimension(a.getDimensionPixelSize(R.styleable.Formats_formatDimension, -1));
        }
        else {
            getView().formatDimension(res.getDimensionPixelSize(R.dimen.format_dimension));
        }
        if (a.hasValue(R.styleable.Formats_formatEnum)) {
            getView().formatEnum(a.getInt(R.styleable.Formats_formatEnum, -1));
        }
        else {
            getView().formatEnum(res.getInteger(R.integer.format_enum));
        }
        if (a.hasValue(R.styleable.Formats_formatFlag)) {
            getView().formatFlag(a.getInt(R.styleable.Formats_formatFlag, -1));
        }
        else {
            getView().formatFlag(res.getInteger(R.integer.format_flag));
        }
        if (a.hasValue(R.styleable.Formats_formatFraction)) {
            getView().formatFraction(a.getFraction(R.styleable.Formats_formatFraction, 2, 3, -1f));
        }
        else {
            getView().formatFraction(res.getFraction(R.fraction.format_fraction, 2, 3));
        }
        if (a.hasValue(R.styleable.Formats_formatInteger)) {
            getView().formatInteger(a.getInt(R.styleable.Formats_formatInteger, -1));
        }
        else {
            getView().formatInteger(res.getInteger(R.integer.format_integer));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getView().formatReference_CharSequenceArray(a.getTextArray(R.styleable.Formats_formatReference));
        }
        else {
            getView().formatReference_CharSequenceArray(res.getTextArray(R.array.format_string_array));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getView().formatReference_ColorStateList(a.getColorStateList(R.styleable.Formats_formatReference));
        }
        else {
            getView().formatReference_ColorStateList(res.getColorStateList(R.color.format_color_state_list));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getView().formatReference_Drawable(a.getDrawable(R.styleable.Formats_formatReference));
        }
        else {
            getView().formatReference_Drawable(res.getDrawable(R.drawable.format_drawable));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getView().formatReference_int(a.getResourceId(R.styleable.Formats_formatReference, -1));
        }
        else {
            getView().formatReference_int(R.bool.active);
        }
        if (a.hasValue(R.styleable.Formats_formatString)) {
            getView().formatString_CharSequence(a.getText(R.styleable.Formats_formatString));
        }
        else {
            getView().formatString_CharSequence(res.getText(R.string.format_char_sequence));
        }
        if (a.hasValue(R.styleable.Formats_formatString)) {
            getView().formatString_String(a.getString(R.styleable.Formats_formatString));
        }
        else {
            getView().formatString_String(res.getString(R.string.format_string));
        }
    }

    @Override
    protected void applyParent(Style style) {
        new ViewStyleApplier(getView()).apply(style);
    }
}