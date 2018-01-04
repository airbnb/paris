package com.airbnb.paris.proxies

import android.content.*
import android.support.test.*
import android.support.test.runner.*
import android.view.*
import android.widget.*
import org.junit.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class GenericViewProxyTests {

    private val context = InstrumentationRegistry.getTargetContext()!!

    @Test
    fun assertViewProxySet() {
        assertProxySet(VIEW_MAPPINGS as List<BaseViewMapping<Any, ViewProxy, View, Any>>, VIEW_SETUPS)
    }

    @Test
    fun assertViewGroupProxySet() {
        assertProxySet(VIEW_GROUP_MAPPINGS as List<BaseViewMapping<Any, ViewGroupProxy, ViewGroup, Any>>)
    }

    @Test
    fun assertTextViewProxySet() {
        assertProxySet(TEXT_VIEW_MAPPINGS as List<BaseViewMapping<Any, TextViewProxy, TextView, Any>>)
    }

    @Test
    fun assertImageViewProxySet() {
        assertProxySet(IMAGE_VIEW_MAPPINGS as List<BaseViewMapping<Any, ImageViewProxy, ImageView, Any>>)
    }

    private inline fun <reified P : Proxy<P, V>, reified V : View> assertProxySet(mappings: List<BaseViewMapping<Any, P, V, Any>>, setups: List<(V) -> Unit> = listOf({ _ -> })) {
        for (setup in setups) {
            for (mapping in mappings) {
                assertProxySet(mapping, setup)
            }
        }
    }

    private inline fun <reified P : Proxy<P, V>, reified V : View> assertProxySet(mapping: BaseViewMapping<Any, P, V, Any>, noinline setup: (V) -> Unit) {
        val view: V
        if (V::class == ViewGroup::class) {
            // ViewGroup is abstract so we can't instantiate it
            view = FrameLayout(context) as V
        } else {
            view = V::class.java.getDeclaredConstructor(Context::class.java).newInstance(context)
        }
        setup(view)

        val proxy = P::class.java.getDeclaredConstructor(V::class.java).newInstance(view)

        mapping.testValues.forEach {

            mapping.setProxyFunction(proxy, it)

            if (proxy is ViewProxy) {
                // The implementation doesn't actually need the style so we pass null for convenience
                proxy.afterStyle(null)
            } else if (proxy is TextViewProxy) {
                // The implementation doesn't actually need the style so we pass null for convenience
                proxy.afterStyle(null)
            }

            mapping.assertViewSet(view, it)
        }
    }
}
