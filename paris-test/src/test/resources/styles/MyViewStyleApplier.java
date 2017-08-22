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

    public void applyRed() {
        apply(R.style.MyView_Red);
    }

    public void applyGreen() {
        apply(R.style.MyView_Green);
    }

    public void applyBlue() {
        apply(R.style.MyView_Blue);
    }

    public abstract static class BaseStyleBuilder<B extends BaseStyleBuilder<B, A>, A extends StyleApplier<?, ?>> extends ViewProxyStyleApplier.BaseStyleBuilder<B, A> {
        public BaseStyleBuilder(A applier) {
            super(applier);
        }

        public BaseStyleBuilder() {
        }

        public B addRed() {
            add(R.style.MyView_Red);
            return (B) this;
        }

        public B addGreen() {
            add(R.style.MyView_Green);
            return (B) this;
        }

        public B addBlue() {
            add(R.style.MyView_Blue);
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