package com.airbnb.paris.views.kotlin

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.airbnb.paris.R2
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable

@Styleable("Test_WithInternalAttrFunctionView")
class WithInternalAttrFunctionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    var arbitraryBooleanValue = false

    @Attr(R2.styleable.Test_WithInternalAttrFunctionView_test_arbitraryBoolean)
    internal fun setArbitraryBoolean(value: Boolean) {
        arbitraryBooleanValue = value
    }
}
