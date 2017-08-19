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

    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        title = new TextView(getContext());
    }

    @Attr(value = R2.styleable.MyView_active, defaultValue = R2.bool.active)
    public void setActive(boolean active) {
        // Nothing to do
    }
}
