package com.airbnb.paris.test

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Style
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.extensions.myViewStyle

@Styleable("Formats")
abstract class MyViewSuper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    @Attr(value = R2.styleable.Formats_formatBoolean2)
    fun setInverse(inverse: Boolean) {
    }

    protected open fun showDivider(show: Boolean) {
    }
}