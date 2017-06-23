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
        return R.styleable.MyView;
    }

    @Override
    protected void processAttributes(Style style, TypedArrayWrapper a) {
        Resources res = getViewOrThrow().getContext().getResources();
        if (a.hasValue(R.styleable.MyView_active)) {
            getViewOrThrow().setActive(a.getBoolean(R.styleable.MyView_active, false));
        }
        else {
            getViewOrThrow().setActive(res.getBoolean(R.bool.active));
        }
        if (a.hasValue(R.styleable.MyView_title)) {
            getViewOrThrow().setTitle(a.getString(R.styleable.MyView_title));
        }
        else {
            getViewOrThrow().setTitle(res.getString(R.string.app_name));
        }
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