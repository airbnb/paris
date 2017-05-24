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
        if (a.hasValue(R.styleable.Formats_formatColor)) {
            getView().formatColor(a.getColor(R.styleable.Formats_formatColor, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatDimension)) {
            getView().formatDimension(a.getDimensionPixelSize(R.styleable.Formats_formatDimension, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatEnum)) {
            getView().formatEnum(a.getInt(R.styleable.Formats_formatEnum, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatFlag)) {
            getView().formatFlag(a.getInt(R.styleable.Formats_formatFlag, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatFloat)) {
            getView().formatFloat(a.getFloat(R.styleable.Formats_formatFloat, -1f));
        }
        if (a.hasValue(R.styleable.Formats_formatFraction)) {
            getView().formatFraction(a.getFraction(R.styleable.Formats_formatFraction, 2, 3, -1f));
        }
        if (a.hasValue(R.styleable.Formats_formatInteger)) {
            getView().formatInteger(a.getInt(R.styleable.Formats_formatInteger, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getView().formatReference_CharSequenceArray(a.getTextArray(R.styleable.Formats_formatReference));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getView().formatReference_ColorStateList(a.getColorStateList(R.styleable.Formats_formatReference));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getView().formatReference_Drawable(a.getDrawable(R.styleable.Formats_formatReference));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getView().formatReference_int(a.getInt(R.styleable.Formats_formatReference, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatString)) {
            getView().formatString_CharSequence(a.getText(R.styleable.Formats_formatString));
        }
        if (a.hasValue(R.styleable.Formats_formatString)) {
            getView().formatString_String(a.getString(R.styleable.Formats_formatString));
        }
    }

    @Override
    protected void applyParent(Style style) {
        new ViewStyleApplier(getView()).apply(style);
    }
}