package com.airbnb.paris.test;

import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.proxies.ViewProxyStyleApplier;
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

    public void applyRedStyle() {
        apply(MyView.RED_STYLE);
    }

    public void applyGreenStyle() {
        apply(MyView.greenStyle);
    }

    public void applyBlue() {
        StyleBuilder builder = new StyleBuilder();
        MyView.blue(builder);
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

        public StyleBuilder addRedStyle() {
            add(MyView.RED_STYLE);
            return this;
        }

        public StyleBuilder addGreenStyle() {
            add(MyView.greenStyle);
            return this;
        }

        public StyleBuilder addBlue() {
            consumeSimpleStyleBuilder();
            debugName("Blue");
            MyView.blue(this);
            consumeSimpleStyleBuilder();
            return this;
        }
    }
}