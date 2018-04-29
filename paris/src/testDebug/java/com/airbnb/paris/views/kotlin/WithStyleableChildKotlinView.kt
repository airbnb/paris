package com.airbnb.paris.views.kotlin

import android.content.Context
import android.util.AttributeSet
import android.view.View

import com.airbnb.paris.R2
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.annotations.StyleableChild

/**
 * The name is appended with Kotlin because there's already a [WithStyleableChildView] which would
 * create a conflict with the generated style extensions
 */
@Styleable("Test_WithStyleableChildKotlinView")
class WithStyleableChildKotlinView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    @StyleableChild(R2.styleable.Test_WithStyleableChildKotlinView_test_arbitraryStyle)
    val arbitrarySubView = View(context)
}
