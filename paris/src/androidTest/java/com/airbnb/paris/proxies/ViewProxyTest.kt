package com.airbnb.paris.proxies

import android.content.*
import android.content.res.*
import android.support.test.*
import android.support.test.runner.*
import android.view.*
import org.junit.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class ViewProxyTest {

    private lateinit var context: Context
    private lateinit var res: Resources
    private lateinit var view: View
    private lateinit var proxy: ViewProxy

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        res = context.resources
        view = View(context)
        proxy = ViewProxy(view)
    }

    @Test
    fun auto() {
        for (mapping in VIEW_MAPPINGS) {
            mapping as BaseViewMapping<Any, Any, View, Any>

            setup()

            mapping.testValues.forEach {
                mapping.setProxyFunction(proxy, it)
                // Assumes the style parameter isn't used
                proxy.afterStyle(null)
                mapping.assertViewSet(view, it)
            }
        }
    }
}
