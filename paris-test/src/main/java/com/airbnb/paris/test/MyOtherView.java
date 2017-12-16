package com.airbnb.paris.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.annotations.StyleableChild;

@Styleable("MyView")
public class MyOtherView extends View {

    @StyleableChild(R2.styleable.MyView_titleStyle)
    public TextView title;

    public MyOtherView(Context context) {
        super(context);
        init();
    }

    public MyOtherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyOtherView(Context context, AttributeSet attrs, int defStyle) {
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
