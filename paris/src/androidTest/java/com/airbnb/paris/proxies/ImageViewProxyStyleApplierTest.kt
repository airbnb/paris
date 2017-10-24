package com.airbnb.paris.proxies

import android.support.test.*
import android.support.test.runner.*
import android.widget.*
import com.airbnb.paris.proxies.ImageViewProxyStyleApplier.*
import org.junit.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class ImageViewProxyStyleApplierTest {

    private val context = InstrumentationRegistry.getTargetContext()!!
    private lateinit var view: ImageView
    private lateinit var styleApplier: ImageViewProxyStyleApplier
    private lateinit var styleBuilder: StyleBuilder

    @Before
    fun setup() {
        view = ImageView(context)
        styleApplier = ImageViewProxyStyleApplier(view)
        styleBuilder = StyleBuilder()
    }

    @Test
    fun auto() {
        for (mapping in (VIEW_MAPPINGS + IMAGE_VIEW_MAPPINGS)) {
            mapping as BaseViewMapping<Any, *, ImageView, Any>

            setup()

            mapping.testValues.forEach {
                // Set the value on the style builder
                mapping.setStyleBuilderValueFunction(styleBuilder, it)
                // Apply the style to the view
                styleApplier.apply(styleBuilder.build())
                // Check that the value was correctly applied
                mapping.assertViewSet(view, it)
            }
        }
    }
}
