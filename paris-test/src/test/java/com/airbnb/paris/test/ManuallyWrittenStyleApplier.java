package com.airbnb.paris.test;

import android.view.View;

import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.TypedArrayWrapper;

public final class ManuallyWrittenStyleApplier extends StyleApplier<ManuallyWrittenStyleApplier, View, View> {
    public ManuallyWrittenStyleApplier(View view) {
        super(view);
    }

    @Override
    protected int[] attributes() {
        return R.styleable.ManuallyWritten;
    }

    @Override
    protected void processAttributes(Style style, TypedArrayWrapper a) {
        if (a.hasValue(R.styleable.ManuallyWritten_attribute1)) {
            // nothing!
        }
    }
}