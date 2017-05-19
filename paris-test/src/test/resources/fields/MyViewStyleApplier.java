package com.airbnb.paris.test;

import com.airbnb.paris.Paris;
import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.TextViewStyleApplier;
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
        if (index == R.styleable.MyView_active) {
            getView().active = a.getBoolean(index, false);
        } else if (index == R.styleable.MyView_factor) {
            getView().factor = a.getFloat(index, -1f);
        } else if (index == R.styleable.MyView_image) {
            getView().image = a.getDrawable(index);
        } else if (index == R.styleable.MyView_index) {
            getView().index = a.getInt(index, -1);
        } else if (index == R.styleable.MyView_title) {
            getView().titleText = a.getString(index);
        } else if (index == R.styleable.MyView_titleStyle) {
            Paris.style(getView().titleView).apply(a.getResourceId(index, -1));
        } else if (index == R.styleable.MyView_subtitle) {
            getView().subtitleText = a.getString(index);
        } else if (index == R.styleable.MyView_subtitleStyle) {
            Paris.style(getView().subtitleView).apply(a.getResourceId(index, -1));
        } else if (index == R.styleable.MyView_verticalPadding) {
            getView().verticalPaddingPx = a.getDimensionPixelSize(index, -1);
        }
    }

    @Override
    protected void applyParent(Style style) {
        new ViewStyleApplier(getView()).apply(style);
    }

    public TextViewStyleApplier titleView() {
        return new TextViewStyleApplier(getView().titleView);
    }

    public TextViewStyleApplier subtitleView() {
        return new TextViewStyleApplier(getView().subtitleView);
    }
}