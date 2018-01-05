package com.airbnb.parislibrary;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.paris.annotations.Style;
import com.airbnb.paris.annotations.Styleable;

@Styleable
public class CustomView extends android.support.v7.widget.AppCompatTextView {

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
