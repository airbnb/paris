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
            getView().setActive(a.getBoolean(R.styleable.MyView_active, false));
        }
        if (a.hasValue(R.styleable.MyView_factor)) {
            getView().setFactor(a.getFloat(R.styleable.MyView_factor, -1f));
        }
        if (a.hasValue(R.styleable.MyView_image)) {
            getView().setDrawable(a.getDrawable(R.styleable.MyView_image));
        }
        if (a.hasValue(R.styleable.MyView_index)) {
            getView().setIndex(a.getInt(R.styleable.MyView_index, -1));
        }
        if (a.hasValue(R.styleable.MyView_title)) {
            getView().setTitle(a.getString(R.styleable.MyView_title));
        }
        if (a.hasValue(R.styleable.MyView_titleStyle)) {
            getView().setTitleStyle(a.getResourceId(R.styleable.MyView_titleStyle, -1));
        }
        if (a.hasValue(R.styleable.MyView_subtitle)) {
            getView().setSubtitle(a.getString(R.styleable.MyView_subtitle));
        }
        if (a.hasValue(R.styleable.MyView_subtitleStyle)) {
            getView().setSubtitleStyle(a.getResourceId(R.styleable.MyView_subtitleStyle, -1));
        }
        if (a.hasValue(R.styleable.MyView_verticalPadding)) {
            getView().setVerticalPaddingRes(a.getResourceId(R.styleable.MyView_verticalPadding, -1));
        }
        if (a.hasValue(R.styleable.MyView_verticalPadding)) {
            getView().setVerticalPadding(a.getDimensionPixelSize(R.styleable.MyView_verticalPadding, -1));
        }
    }

    @Override
    protected void applyParent(Style style) {
        new ViewStyleApplier(getView()).apply(style);
    }
}