package com.airbnb.paris.test;

import android.content.res.Resources;
import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.TypedArrayWrapper;
import com.airbnb.paris.proxy.TextViewProxyStyleApplier;
import com.airbnb.paris.proxy.ViewProxyStyleApplier;
import java.lang.Override;

public final class MyViewStyleApplier extends StyleApplier<MyViewStyleApplier, MyView, MyView> {
    public MyViewStyleApplier(MyView view) {
        super(view);
    }

    @Override
    protected int[] attributes() {
        return R.styleable.MyView;
    }

    @Override
    public int[] attributesWithDefaultValue() {
        return new int[] {};
    }

    @Override
    protected void processAttributes(Style style, TypedArrayWrapper a) {
        Resources res = getView().getContext().getResources();
        Style subStyle;
        if (a.hasValue(R.styleable.MyView_titleStyle)) {
            subStyle = new Style(a.getResourceId(R.styleable.MyView_titleStyle, -1));
            subStyle.setDebugListener(style.getDebugListener());
            title().apply(subStyle);
        }
        if (a.hasValue(R.styleable.MyView_subtitleStyle)) {
            subStyle = new Style(a.getResourceId(R.styleable.MyView_subtitleStyle, -1));
            subStyle.setDebugListener(style.getDebugListener());
            subtitle().apply(subStyle);
        }
        if (a.hasValue(R.styleable.MyView_dividerStyle)) {
            subStyle = new Style(a.getResourceId(R.styleable.MyView_dividerStyle, -1));
            subStyle.setDebugListener(style.getDebugListener());
            divider().apply(subStyle);
        }
    }

    @Override
    protected void applyParent(Style style) {
        new ViewProxyStyleApplier(getView()).apply(style);
    }

    public TextViewProxyStyleApplier title() {
        return new TextViewProxyStyleApplier(getProxy().title);
    }

    public TextViewProxyStyleApplier subtitle() {
        return new TextViewProxyStyleApplier(getProxy().subtitle);
    }

    public ViewProxyStyleApplier divider() {
        return new ViewProxyStyleApplier(getProxy().divider);
    }
}