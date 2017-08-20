package com.airbnb.paris.test;

import android.content.res.Resources;
import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.TypedArrayWrapper;
import com.airbnb.paris.proxy.ViewProxyStyleApplier;
import java.lang.Override;

public final class MyViewStyleApplier extends StyleApplier<MyViewStyleApplier, MyView, MyView> {
    public MyViewStyleApplier(MyView view) {
        super(view);
    }

    @Override
    protected int[] attributes() {
        return R.styleable.Formats;
    }

    @Override
    public int[] attributesWithDefaultValue() {
        return new int[] {};
    }

    @Override
    protected void processAttributes(Style style, TypedArrayWrapper a) {
        Resources res = getView().getContext().getResources();
        if (a.hasValue(R.styleable.Formats_formatBoolean)) {
            getProxy().formatBoolean(a.getBoolean(R.styleable.Formats_formatBoolean, false));
        }
        if (a.hasValue(R.styleable.Formats_formatColor)) {
            getProxy().formatColor(a.getColor(R.styleable.Formats_formatColor, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatDimension)) {
            getProxy().formatDimension(a.getDimensionPixelSize(R.styleable.Formats_formatDimension, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatEnum)) {
            getProxy().formatEnum(a.getInt(R.styleable.Formats_formatEnum, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatFlag)) {
            getProxy().formatFlag(a.getInt(R.styleable.Formats_formatFlag, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatFloat)) {
            getProxy().formatFloat(a.getFloat(R.styleable.Formats_formatFloat, -1f));
        }
        if (a.hasValue(R.styleable.Formats_formatFraction)) {
            getProxy().formatFraction(a.getFraction(R.styleable.Formats_formatFraction, 2, 3, -1f));
        }
        if (a.hasValue(R.styleable.Formats_formatInteger)) {
            getProxy().formatInteger(a.getInt(R.styleable.Formats_formatInteger, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getProxy().formatReference_CharSequenceArray(a.getTextArray(R.styleable.Formats_formatReference));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getProxy().formatReference_ColorStateList(a.getColorStateList(R.styleable.Formats_formatReference));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getProxy().formatReference_Drawable(a.getDrawable(R.styleable.Formats_formatReference));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getProxy().formatReference_int(a.getResourceId(R.styleable.Formats_formatReference, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatString)) {
            getProxy().formatString_CharSequence(a.getText(R.styleable.Formats_formatString));
        }
        if (a.hasValue(R.styleable.Formats_formatString)) {
            getProxy().formatString_String(a.getString(R.styleable.Formats_formatString));
        }
    }

    @Override
    protected void applyParent(Style style) {
        new ViewProxyStyleApplier(getView()).apply(style);
    }
}