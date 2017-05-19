package com.airbnb.paris.test;

import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import java.lang.Override;

public final class MyViewDependencyNoAttrsStyleApplier extends StyleApplier<MyView> {
    public MyViewDependencyNoAttrsStyleApplier(MyView view) {
        super(view);
    }

}