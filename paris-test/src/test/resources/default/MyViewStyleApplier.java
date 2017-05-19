package com.airbnb.paris.test;

import android.content.res.Resources;
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
    protected void processAttributes(Style style, TypedArrayWrapper a) {
        Resources res = getView().getContext().getResources();
        if (a.hasValue(R.styleable.MyView_active)) {
            getView().active = a.getBoolean(R.styleable.MyView_active, false);
        }
        else {
            getView().active = res.getBoolean(R.bool.active);
        }
        if (a.hasValue(R.styleable.MyView_title)) {
            getView().setTitle(a.getString(R.styleable.MyView_title));
        }
        else {
            getView().setTitle(res.getString(R.string.app_name));
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