package com.airbnb.paris.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Styleable;

@Styleable(dependencies = FontStyleApplier.class)
public class MyViewDependencyNoAttrs extends View {

    @Attr(R2.styleable.Font_font)
    TextView title;

    public MyViewDependencyNoAttrs(Context context) {
        super(context);
    }

    public MyViewDependencyNoAttrs(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewDependencyNoAttrs(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}