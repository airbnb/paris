package com.airbnb.paris.proxy

import android.graphics.drawable.Drawable
import android.view.View
import io.kotlintest.specs.StringSpec
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class ViewProxyTest : StringSpec() {

    init {
        val view = mock(View::class.java)!!
        val proxy = ViewProxy(view)

        "setBackground call proxied" {
            val drawable = mock(Drawable::class.java)
            proxy.setBackground(drawable)
            verify(view).background = drawable
        }

        "setPaddingBottom call proxied" {
            proxy.setPaddingBottom(5)
            verify(view).setPadding(0, 0, 0, 5)
        }

        // TODO Test other proxy methods
    }
}
