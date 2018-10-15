package com.airbnb.paris.views.kotlin

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.airbnb.paris.annotations.Style
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.extensions.withStylePropertyViewStyle

@Styleable
class WithStyleInternalPropertyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    companion object {
        @Style
        internal val testStyle = withStylePropertyViewStyle {}
    }
}
