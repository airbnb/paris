package com.airbnb.paris.test;

import android.content.res.Resources;
import com.airbnb.paris.Paris;
import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.TextViewStyleApplier;
import com.airbnb.paris.TypedArrayWrapper;
import com.airbnb.paris.ViewStyleApplier;
import java.lang.Override;

public final class MyViewStyleApplier extends StyleApplier<MyViewStyleApplier, MyView> {
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
        if (a.hasValue(R.styleable.MyView_titleStyle)) {
            Paris.style(getView().title).apply(a.getResourceId(R.styleable.MyView_titleStyle, -1));
        }
        if (a.hasValue(R.styleable.MyView_subtitleStyle)) {
            Paris.style(getView().subtitle).apply(a.getResourceId(R.styleable.MyView_subtitleStyle, -1));
        }
        if (a.hasValue(R.styleable.MyView_dividerStyle)) {
            Paris.style(getView().divider).apply(a.getResourceId(R.styleable.MyView_dividerStyle, -1));
        }
    }

    @Override
    protected void applyParent(Style style) {
        new ViewStyleApplier(getView()).apply(style);
    }

    public TextViewStyleApplier title() {
        return new TextViewStyleApplier(getView().title);
    }

    public TextViewStyleApplier subtitle() {
        return new TextViewStyleApplier(getView().subtitle);
    }

    public ViewStyleApplier divider() {
        return new ViewStyleApplier(getView().divider);
    }
}