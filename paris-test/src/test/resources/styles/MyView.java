package com.airbnb.paris.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.paris.annotations.Style;
import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.test.MyViewStyleApplier.StyleBuilder;

@Styleable
public class MyView extends View {

    @Style
    static int green = R2.style.MyView_Green;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Style
    static void red(StyleBuilder builder) {
    }
}