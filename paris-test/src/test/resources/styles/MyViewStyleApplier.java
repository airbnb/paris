
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

    public void applyRed() {
        apply(R.style.MyView_Red);
    }

    public void applyGreen() {
        apply(R.style.MyView_Green);
    }

    public void applyBlue() {
        apply(R.style.MyView_Blue);
    }
}