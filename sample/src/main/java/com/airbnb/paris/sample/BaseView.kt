package com.airbnb.paris.sample

import android.content.Context
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout

import com.airbnb.paris.annotations.Styleable

@Styleable
abstract class BaseView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(layout(), this, true)
        init(attrs)
    }

    protected abstract fun init(attrs: AttributeSet?)

    @LayoutRes
    abstract fun layout(): Int
}
