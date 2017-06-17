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
        Style subStyle;
        if (a.hasValue(R.styleable.MyView_titleStyle)) {
            subStyle = new Style(a.getResourceId(R.styleable.MyView_titleStyle, -1));
            subStyle.setDebugListener(style.getDebugListener());
            Paris.style(getView().title).apply(subStyle);
        }
        if (a.hasValue(R.styleable.MyView_subtitleStyle)) {
            subStyle = new Style(a.getResourceId(R.styleable.MyView_subtitleStyle, -1));
            subStyle.setDebugListener(style.getDebugListener());
            Paris.style(getView().subtitle).apply(subStyle);
        }
        if (a.hasValue(R.styleable.MyView_dividerStyle)) {
            subStyle = new Style(a.getResourceId(R.styleable.MyView_dividerStyle, -1));
            subStyle.setDebugListener(style.getDebugListener());
            Paris.style(getView().divider).apply(subStyle);
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