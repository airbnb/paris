package com.airbnb.paris.test;

import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.proxy.ViewProxyStyleApplier;
import java.lang.Override;

public final class MyViewStyleApplier extends StyleApplier<MyView, MyView> {
    public MyViewStyleApplier(MyView view) {
        super(view);
    }

    @Override
    protected void applyParent(Style style) {
        new ViewProxyStyleApplier(getView()).apply(style);
    }

    public StyleBuilder builder() {
        return new StyleBuilder(this);
    }

    public void applyGreen() {
        apply(MyView.green);
    }

    public void applyRed() {
        StyleBuilder builder = new StyleBuilder();
        MyView.red(builder);
        apply(builder.build());
    }

    public abstract static class BaseStyleBuilder<B extends BaseStyleBuilder<B, A>, A extends StyleApplier<?, ?>> extends ViewProxyStyleApplier.BaseStyleBuilder<B, A> {
        public BaseStyleBuilder(A applier) {
            super(applier);
        }

        public BaseStyleBuilder() {
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

        public StyleBuilder addGreen() {
            add(MyView.green);
            return this;
        }

        public StyleBuilder addRed() {
            MyView.red(this);
            return this;
        }
    }
}