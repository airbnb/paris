package com.airbnb.paris.test;

import android.view.View;

import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.TypedArrayWrapper;

public final class FontStyleApplier extends StyleApplier<View> {
    public FontStyleApplier(View view) {
        super(view);
    }

    @Override
    protected int[] attributes() {
        return R.styleable.Font;
    }

    @Override
    protected void processAttribute(Style style, TypedArrayWrapper a, int index) {
        if (index == R.styleable.Font_font) {
            // nothing!
        }
    }
}