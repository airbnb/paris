package com.airbnb.paris.test;

import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.TypedArrayWrapper;
import com.airbnb.paris.ViewStyleApplier;
import java.lang.Override;

public final class MyViewStyleApplier extends StyleApplier<MyView> {
    public MyViewStyleApplier(MyView view) {
        super(view);
    }

    @Override
    protected int[] attributes() {
        return R.styleable.MyView;
    }

    @Override
    protected void processAttribute(Style style, TypedArrayWrapper a, int index) {
        if (index == R.styleable.MyView_title) {
            getView().setTitle(a.getString(index));
        }
    }

    @Override
    protected void applyParent(Style style) {
        new ViewStyleApplier(getView()).apply(style);
    }

    @Override
    protected void applyDependencies(Style style) {
        new ManuallyWrittenStyleApplier(getView()).apply(style);
    }
}