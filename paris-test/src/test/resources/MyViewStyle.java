package com.airbnb.paris.test;

import android.content.Context;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.Style.Config;
import com.google.auto.value.AutoValue;

@AutoValue
public final class MyViewStyle extends BaseStyle<MyView> {

    public static MyViewStyle from(AttributeSet set, @StyleRes int styleRes, Config config) {
        return new AutoValue_MyViewStyle(set, styleRes, config);
    }

    @Override
    protected Class<MyView> targetClass() {
        return MyView.class;
    }

    @Override
    protected int[] attributes() {
        return R.styleable.MyView;
    }
}
