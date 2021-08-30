package com.airbnb.paris.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.paris.annotations.Style;
import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.test.MyViewStyleApplier.StyleBuilder;

@Styleable
public class MyView extends View {

    @Style(isDefault = true)
    static final int RED_STYLE = R2.style.MyView_Red;

    @Style
    static final int greenStyle = R2.style.MyView_Red;

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
    static void blue(StyleBuilder builder) {
    }
}