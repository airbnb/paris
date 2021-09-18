package com.airbnb.paris.test

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Dimension
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Style
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.extensions.myViewStyle

@Styleable("Formats")
class MyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    @Attr(R2.styleable.Formats_formatBoolean)
     fun showDivider(show: Boolean)  {

    }

    @Attr(R2.styleable.Formats_formatBoolean2)
    fun showDivider2(show: Boolean)  {

    }

    companion object {
        private fun foo(bar: Int = 1) = myViewStyle {
            println(bar)
        }

        @Style(isDefault = true)
        val testStyle = foo()
        @Style
        val testStyle2 = foo(2)
        @Style
        val testStyle23 = foo(3)
        @Style
        val testStyle234 = foo()
        @Style
        val testStyle2345 = foo()
        @Style
        val testStyle23456 = foo()


    }
}