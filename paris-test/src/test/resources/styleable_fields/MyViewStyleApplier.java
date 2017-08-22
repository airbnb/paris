package com.airbnb.paris.test;

import android.content.res.Resources;
import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.TypedArrayWrapper;
import com.airbnb.paris.proxy.TextViewProxyStyleApplier;
import com.airbnb.paris.proxy.ViewProxyStyleApplier;
import java.lang.Override;
import java.lang.Void;
import kotlin.jvm.functions.Function1;

public final class MyViewStyleApplier extends StyleApplier<MyView, MyView> {
    public MyViewStyleApplier(MyView view) {
        super(view);
    }

    @Override
    protected void applyParent(Style style) {
        new ViewProxyStyleApplier(getView()).apply(style);
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
    protected void processStyleableFields(Style style, TypedArrayWrapper a) {
        Resources res = getView().getContext().getResources();
        Style subStyle;
        if (a.hasValue(R.styleable.MyView_titleStyle)) {
            subStyle = a.getStyle(R.styleable.MyView_titleStyle);
            subStyle.setDebugListener(style.getDebugListener());
            title().apply(subStyle);
        }
        if (a.hasValue(R.styleable.MyView_subtitleStyle)) {
            subStyle = a.getStyle(R.styleable.MyView_subtitleStyle);
            subStyle.setDebugListener(style.getDebugListener());
            subtitle().apply(subStyle);
        }
        if (a.hasValue(R.styleable.MyView_dividerStyle)) {
            subStyle = a.getStyle(R.styleable.MyView_dividerStyle);
            subStyle.setDebugListener(style.getDebugListener());
            divider().apply(subStyle);
        }
    }

    @Override
    protected void processAttributes(Style style, TypedArrayWrapper a) {
        Resources res = getView().getContext().getResources();
    }

    public StyleBuilder builder() {
        return new StyleBuilder(this);
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

    public abstract static class BaseStyleBuilder<B extends BaseStyleBuilder<B, A>, A extends StyleApplier<?, ?>> extends ViewProxyStyleApplier.BaseStyleBuilder<B, A> {
        public BaseStyleBuilder(A applier) {
            super(applier);
        }

        public BaseStyleBuilder() {
        }

        public B titleStyle(int value) {
            getBuilder().put(R.styleable.MyView_titleStyle, value);
            return (B) this;
        }

        public B titleStyle(Style value) {
            getBuilder().put(R.styleable.MyView_titleStyle, value);
            return (B) this;
        }

        public B titleStyle(Function1<TextViewProxyStyleApplier.StyleBuilder, Void> function) {
            TextViewProxyStyleApplier.StyleBuilder subBuilder = new TextViewProxyStyleApplier.StyleBuilder();
            function.invoke(subBuilder);
            getBuilder().put(R.styleable.MyView_titleStyle, subBuilder.build());
            return (B) this;
        }

        public B subtitleStyle(int value) {
            getBuilder().put(R.styleable.MyView_subtitleStyle, value);
            return (B) this;
        }

        public B subtitleStyle(Style value) {
            getBuilder().put(R.styleable.MyView_subtitleStyle, value);
            return (B) this;
        }

        public B subtitleStyle(Function1<TextViewProxyStyleApplier.StyleBuilder, Void> function) {
            TextViewProxyStyleApplier.StyleBuilder subBuilder = new TextViewProxyStyleApplier.StyleBuilder();
            function.invoke(subBuilder);
            getBuilder().put(R.styleable.MyView_subtitleStyle, subBuilder.build());
            return (B) this;
        }

        public B dividerStyle(int value) {
            getBuilder().put(R.styleable.MyView_dividerStyle, value);
            return (B) this;
        }

        public B dividerStyle(Style value) {
            getBuilder().put(R.styleable.MyView_dividerStyle, value);
            return (B) this;
        }

        public B dividerStyle(Function1<ViewProxyStyleApplier.StyleBuilder, Void> function) {
            ViewProxyStyleApplier.StyleBuilder subBuilder = new ViewProxyStyleApplier.StyleBuilder();
            function.invoke(subBuilder);
            getBuilder().put(R.styleable.MyView_dividerStyle, subBuilder.build());
            return (B) this;
        }

        public B applyTo(MyView view) {
            new MyViewStyleApplier(view).apply(build());
            return (B) this;
        }
    }

    public static final class StyleBuilder extends BaseStyleBuilder<StyleBuilder, MyViewStyleApplier> {
        public StyleBuilder(MyViewStyleApplier applier) {
            super(applier);
        }

        public StyleBuilder() {
        }
    }
}