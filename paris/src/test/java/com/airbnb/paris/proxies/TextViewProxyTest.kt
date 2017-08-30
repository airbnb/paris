package com.airbnb.paris.proxies

import android.graphics.drawable.Drawable
import android.widget.TextView
import io.kotlintest.specs.StringSpec
import org.mockito.Mockito

class TextViewProxyTest : StringSpec() {

    init {
        val view = Mockito.mock(TextView::class.java)!!
        val proxy = TextViewProxy(view)

        "setDrawableX calls proxied" {
            Mockito.`when`(view.compoundDrawables).thenReturn(Array<Drawable?>(4, { _ -> null }))
            val drawableBottom = Mockito.mock(Drawable::class.java)
            val drawableLeft = Mockito.mock(Drawable::class.java)
            proxy.setDrawableBottom(drawableBottom)
            proxy.setDrawableLeft(drawableLeft)
            proxy.afterStyle(null)
            Mockito.verify(view).setCompoundDrawables(drawableLeft, null, null, drawableBottom)
        }

        // TODO Test other proxy methods
    }
}
