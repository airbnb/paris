package com.airbnb.paris.proxies

import android.support.test.*
import android.support.test.runner.*
import android.widget.*
import org.junit.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class ImageViewProxyTest {

    private val context = InstrumentationRegistry.getTargetContext()!!
    private val res = context.resources!!
    private lateinit var view: ImageView
    private lateinit var proxy: ImageViewProxy

    @Before
    fun setup() {
        view = ImageView(context)
        proxy = ImageViewProxy(view)
    }

    @Test
    fun auto() {
        for (mapping in IMAGE_VIEW_MAPPINGS) {
            mapping as BaseViewMapping<Any, Any, ImageView, Any>

            setup()

            mapping.testValues.forEach {
                mapping.setProxyFunction(proxy, it)
                mapping.assertViewSet(view, it)
            }
        }
    }
}
