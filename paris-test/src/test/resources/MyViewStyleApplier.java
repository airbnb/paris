package com.airbnb.paris.test;

import com.airbnb.paris.Paris;
import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.TypedArrayWrapper;
import java.lang.Override;

public final class MyViewStyleApplier extends StyleApplier<MyView> {

    public MyViewStyleApplier(MyView view) {
        super(view)
    }

    @Override
    protected int[] attributes() {
        return R.styleable.MyView;
    }

    @Override
    protected void processAttribute(Style style, TypedArrayWrapper a, int index) {
        if (index == R.styleable.MyView_titleStyle) {
            Paris.change(getView().titleText).apply(a.getResourceId(index, -1));
        }
        else if (index == R.styleable.MyView_subtitle) {
            getView().subtitle = a.getString(index);
        }
        else if (index == R.styleable.MyView_verticalPadding) {
            getView().verticalPadding = a.getDimensionPixelSize(index, -1);
        }
        else if (index == R.styleable.MyView_title) {
            getView().setTitle(a.getString(index));
        }
    }
}
