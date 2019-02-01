package com.airbnb.paris.proxies

import android.widget.LinearLayout
import com.airbnb.paris.R2
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable

@Styleable("Paris_LinearLayout")
class LinearLayoutProxy(view: LinearLayout) : BaseProxy<LinearLayoutProxy, LinearLayout>(view) {

    @Attr(R2.styleable.Paris_LinearLayout_android_gravity)
    fun setGravity(gravity: Int) {
        view.gravity = gravity
    }
}
