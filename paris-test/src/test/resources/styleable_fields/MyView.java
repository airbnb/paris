package com.airbnb.paris.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.annotations.StyleableField;

@Styleable("MyView")
public class MyView extends View {

    @StyleableField(R2.styleable.MyView_titleStyle)
    TextView title;

    @StyleableField(R2.styleable.MyView_subtitleStyle)
    TextView subtitle;

    @StyleableField(R2.styleable.MyView_dividerStyle)
    View divider;

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