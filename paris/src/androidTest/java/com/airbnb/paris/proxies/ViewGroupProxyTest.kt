package com.airbnb.paris.proxies

import android.support.test.*
import android.support.test.runner.*
import android.view.*
import android.widget.*
import org.junit.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class ViewGroupProxyTest {

    private val context = InstrumentationRegistry.getTargetContext()!!
    private lateinit var view: ViewGroup
    private lateinit var proxy: ViewGroupProxy

    @Before
    fun setup() {
        view = FrameLayout(context)
        proxy = ViewGroupProxy(view)
    }

    @Test
    fun auto() {
        for (mapping in VIEW_GROUP_MAPPINGS) {
            mapping as BaseViewMapping<Any, Any, ViewGroup, Any>

            setup()

            mapping.testValues.forEach {
                mapping.setProxyFunction(proxy, it)
                mapping.assertViewSet(view, it)
            }
        }
    }
}
