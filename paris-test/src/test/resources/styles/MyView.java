package com.airbnb.paris.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Style;
import com.airbnb.paris.annotations.Styleable;

@Styleable(styles = {
        @Style(name = "Red", id = R2.style.MyView_Red),
        @Style(name = "Green", id = R2.style.MyView_Green),
        @Style(name = "Blue", id = R2.style.MyView_Blue)
})
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
}