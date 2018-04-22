package com.airbnb.paris;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.annotations.StyleableChild;

@Styleable("Test_WithSubStyleView")
public class WithSubStyleView extends View {

    @StyleableChild(R2.styleable.Test_WithSubStyleView_test_arbitraryStyle)
    public View arbitrarySubView;

    public WithSubStyleView(Context context) {
        super(context);
        init();
    }

    public WithSubStyleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WithSubStyleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        arbitrarySubView = new View(getContext());
    }
}
