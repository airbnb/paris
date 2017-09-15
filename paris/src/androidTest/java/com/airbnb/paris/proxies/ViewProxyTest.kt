package com.airbnb.paris.proxies

import android.support.test.*
import android.support.test.runner.*
import android.view.*
import org.junit.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class ViewProxyTest {

    private val context = InstrumentationRegistry.getTargetContext()!!
    private lateinit var view: View
    private lateinit var proxy: ViewProxy

    @Before
    fun setup() {
        view = View(context)
        proxy = ViewProxy(view)
    }

    @Test
    fun auto() {
        for (mapping in VIEW_MAPPINGS) {
            mapping as BaseViewMapping<Any, Any, View, Any>

            for (setupView in VIEW_SETUPS) {
                mapping.testValues.forEach {

                    setup()
                    setupView(view)

                    mapping.setProxyFunction(proxy, it)
                    // Assumes the style parameter isn't used
                    proxy.afterStyle(null)
                    mapping.assertViewSet(view, it)
                }
            }
        }
    }
}
