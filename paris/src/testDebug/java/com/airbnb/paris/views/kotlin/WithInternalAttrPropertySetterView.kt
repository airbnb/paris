package com.airbnb.paris.views.kotlin

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.airbnb.paris.R2
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable

@Styleable("Test_WithInternalAttrPropertySetterView")
class WithInternalAttrPropertySetterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    internal var arbitraryBoolean: Boolean = false
        @Attr(R2.styleable.Test_WithInternalAttrPropertySetterView_test_arbitraryBoolean) set
}
