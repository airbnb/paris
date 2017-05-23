package com.airbnb.paris.test;

import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.ViewStyleApplier;
import java.lang.Override;

public final class MyViewStyleApplier extends StyleApplier<MyViewStyleApplier, MyView> {
    public MyViewStyleApplier(MyView view) {
        super(view);
    }

    @Override
    protected void applyParent(Style style) {
        new ViewStyleApplier(getView()).apply(style);
    }

    public MyViewStyleApplier applyRed() {
        return apply(R.style.MyView_Red);
    }

    public MyViewStyleApplier applyGreen() {
        return apply(R.style.MyView_Green);
    }

    public MyViewStyleApplier applyBlue() {
        return apply(R.style.MyView_Blue);
    }
}