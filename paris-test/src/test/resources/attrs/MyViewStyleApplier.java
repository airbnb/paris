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

    public MyViewStyleApplier() {
        super(null);
    }

    @Override
    protected int[] attributes() {
        return R.styleable.Formats;
    }

    @Override
    protected void processAttributes(Style style, TypedArrayWrapper a) {
        Resources res = getViewOrThrow().getContext().getResources();
        if (a.hasValue(R.styleable.Formats_formatBoolean)) {
            getViewOrThrow().formatBoolean(a.getBoolean(R.styleable.Formats_formatBoolean, false));
        }
        if (a.hasValue(R.styleable.Formats_formatColor)) {
            getViewOrThrow().formatColor(a.getColor(R.styleable.Formats_formatColor, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatDimension)) {
            getViewOrThrow().formatDimension(a.getDimensionPixelSize(R.styleable.Formats_formatDimension, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatEnum)) {
            getViewOrThrow().formatEnum(a.getInt(R.styleable.Formats_formatEnum, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatFlag)) {
            getViewOrThrow().formatFlag(a.getInt(R.styleable.Formats_formatFlag, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatFloat)) {
            getViewOrThrow().formatFloat(a.getFloat(R.styleable.Formats_formatFloat, -1f));
        }
        if (a.hasValue(R.styleable.Formats_formatFraction)) {
            getViewOrThrow().formatFraction(a.getFraction(R.styleable.Formats_formatFraction, 2, 3, -1f));
        }
        if (a.hasValue(R.styleable.Formats_formatInteger)) {
            getViewOrThrow().formatInteger(a.getInt(R.styleable.Formats_formatInteger, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getViewOrThrow().formatReference_CharSequenceArray(a.getTextArray(R.styleable.Formats_formatReference));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getViewOrThrow().formatReference_ColorStateList(a.getColorStateList(R.styleable.Formats_formatReference));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getViewOrThrow().formatReference_Drawable(a.getDrawable(R.styleable.Formats_formatReference));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getViewOrThrow().formatReference_int(a.getResourceId(R.styleable.Formats_formatReference, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatString)) {
            getViewOrThrow().formatString_CharSequence(a.getText(R.styleable.Formats_formatString));
        }
        if (a.hasValue(R.styleable.Formats_formatString)) {
            getViewOrThrow().formatString_String(a.getString(R.styleable.Formats_formatString));
        }
    }

    @Override
    protected void applyParent(Style style) {
        new ViewStyleApplier(getViewOrThrow()).apply(style);
    }
}