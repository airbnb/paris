package com.airbnb.paris.views.java;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.paris.annotations.Style;
import com.airbnb.paris.annotations.Styleable;

@Styleable
public class WithStyleFieldView extends View {

    @Style
    public static final com.airbnb.paris.styles.Style testStyle = new WithStyleFieldViewStyleApplier.StyleBuilder().build();

    public WithStyleFieldView(Context context) {
        super(context);
    }

    public WithStyleFieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WithStyleFieldView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
