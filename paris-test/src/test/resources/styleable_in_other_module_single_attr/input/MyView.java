package com.airbnb.paris.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Styleable;

@Styleable("MyLibView")
public class MyView extends View {

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Attr(com.airbnb.paris.test.lib.R2.styleable.MyLibView_title)
    public void setTitle(String title) {

    }
}
