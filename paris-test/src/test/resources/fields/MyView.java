package com.airbnb.paris.test;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Styleable;

@Styleable(value = "MyView")
public class MyView extends View {

    @Attr(R2.styleable.MyView_active) boolean active;
    @Attr(R2.styleable.MyView_factor) float factor;
    @Attr(R2.styleable.MyView_image) Drawable image;
    @Attr(R2.styleable.MyView_index) int index;
    @Attr(R2.styleable.MyView_title) String titleText;
    @Attr(R2.styleable.MyView_titleStyle) TextView titleView;
    @Attr(R2.styleable.MyView_subtitle) String subtitleText;
    @Attr(R2.styleable.MyView_subtitleStyle) TextView subtitleView;
    @Attr(R2.styleable.MyView_verticalPadding) @DimenRes int verticalPaddingRes;
    @Attr(R2.styleable.MyView_verticalPadding) @Px int verticalPaddingPx;

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