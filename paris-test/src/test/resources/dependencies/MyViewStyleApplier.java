package com.airbnb.paris.test;

import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
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
    protected void applyParent(Style style) {
        new ViewStyleApplier(getViewOrThrow()).apply(style);
    }

    @Override
    protected void applyDependencies(Style style) {
        new ManuallyWrittenStyleApplier(getViewOrThrow()).apply(style);
    }
}