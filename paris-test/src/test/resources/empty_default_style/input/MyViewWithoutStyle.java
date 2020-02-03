package com.airbnb.paris.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Styleable;

@Styleable(emptyDefaultStyle = true)
public class MyViewWithoutStyle extends View {

    public MyViewWithoutStyle(Context context) {
        super(context);
    }

    public MyViewWithoutStyle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewWithoutStyle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
