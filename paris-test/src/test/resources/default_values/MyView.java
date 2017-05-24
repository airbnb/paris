package com.airbnb.paris.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Styleable;

@Styleable(value = "MyView", dependencies = ManuallyWrittenStyleApplier.class)
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

    @Attr(value = R2.styleable.MyView_active, defaultValue = R2.bool.active)
    public void setActive(boolean active) {}

    @Attr(value = R2.styleable.MyView_title, defaultValue = R2.string.app_name)
    public void setTitle(String title) {}
}