package com.airbnb.paris.proxies

import android.widget.LinearLayout
import androidx.annotation.FloatRange
import com.airbnb.paris.R2
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable

@Styleable("Paris_LinearLayout")
class LinearLayoutProxy(view: LinearLayout): BaseProxy<LinearLayoutProxy, LinearLayout>(view) {

    /**
     * Set layout weight on [LinearLayout].
     *
     * @param weight the weight. Must be a float > 0.0
     */
    @Attr(R2.styleable.Paris_LinearLayout_android_layout_weight)
    fun setLayoutWeight(@FloatRange(from = 0.0) weight: Float) {
        val params = view.layoutParams
        if (params != null) {
            if (params is LinearLayout.LayoutParams) {
                params.weight = weight
            }
            view.layoutParams = params
        }
    }
}
