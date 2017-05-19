package com.airbnb.paris.test;

import android.view.View;

import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.TypedArrayWrapper;

public final class ManuallyWrittenStyleApplier extends StyleApplier<View> {
    public ManuallyWrittenStyleApplier(View view) {
        super(view);
    }

    @Override
    protected int[] attributes() {
        return R.styleable.ManuallyWritten;
    }

    @Override
    protected void processAttribute(Style style, TypedArrayWrapper a, int index) {
        if (index == R.styleable.ManuallyWritten_attribute1) {
            // nothing!
        }
    }
}