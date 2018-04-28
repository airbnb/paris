package com.airbnb.paris.views.kotlin

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.airbnb.paris.R2
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.annotations.StyleableChild

@Styleable("Test_WithStyleableChildPropertyDelegateView")
class WithStyleableChildPropertyDelegateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    @StyleableChild(R2.styleable.Test_WithStyleableChildPropertyDelegateView_test_arbitraryStyle)
    val arbitrarySubView by lazy { View(context) }
}
