package com.airbnb.paris.test

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.airbnb.paris.annotations.Style
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.extensions.myViewStyle

@Styleable
class MyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    companion object {
        @Style
        val testStyle = myViewStyle {}
    }
}