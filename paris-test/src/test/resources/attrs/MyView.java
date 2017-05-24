package com.airbnb.paris.test;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.annotation.Px;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Styleable;

@Styleable("MyView")
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

    @Attr(R2.styleable.MyView_active)
    public void setActive(boolean active) {}

    @Attr(R2.styleable.MyView_factor)
    public void setFactor(float factor) {}

    @Attr(R2.styleable.MyView_image)
    public void setDrawable(Drawable image) {}

    @Attr(R2.styleable.MyView_index)
    public void setIndex(int index) {}

    @Attr(R2.styleable.MyView_title)
    public void setTitle(String titleText) {}

    @Attr(R2.styleable.MyView_titleStyle)
    public void setTitleStyle(@StyleRes int styleRes) {}

    @Attr(R2.styleable.MyView_subtitle)
    public void setSubtitle(String subtitleText) {}

    @Attr(R2.styleable.MyView_subtitleStyle)
    public void setSubtitleStyle(@StyleRes int styleRes) {}

    @Attr(R2.styleable.MyView_verticalPadding)
    public void setVerticalPaddingRes(@DimenRes int verticalPaddingRes) {}

    @Attr(R2.styleable.MyView_verticalPadding)
    public void setVerticalPadding(@Px int verticalPaddingPx) {}
}