package com.airbnb.paris.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.NewStyle;
import com.airbnb.paris.annotations.Style;
import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.test.MyViewStyleApplier.StyleBuilder;

@Styleable(styles = {
        @Style(name = "Blue", id = R2.style.MyView_Blue)
})
public class MyView extends View {

    @NewStyle static int green = R2.style.MyView_Green;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @NewStyle
    static void red(StyleBuilder builder) {
    }
}