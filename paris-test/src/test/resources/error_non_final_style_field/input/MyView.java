package com.airbnb.paris.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Style;
import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.test.MyViewStyleApplier;

@Styleable("MyView")
public class MyView extends View {

    @Style
    static com.airbnb.paris.styles.Style myStyle = new MyViewStyleApplier.StyleBuilder().build();

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
