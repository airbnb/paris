package com.airbnb.paris.views.java;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.paris.R2;
import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.annotations.StyleableChild;

@Styleable("Test_WithStyleableChildView")
public class WithStyleableChildView extends View {

    @StyleableChild(R2.styleable.Test_WithStyleableChildView_test_arbitraryStyle)
    public View arbitrarySubView;

    public WithStyleableChildView(Context context) {
        super(context);
        init();
    }

    public WithStyleableChildView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WithStyleableChildView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        arbitrarySubView = new View(getContext());
    }
}
