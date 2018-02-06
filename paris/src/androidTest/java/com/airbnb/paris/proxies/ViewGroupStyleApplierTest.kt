package com.airbnb.paris.proxies

import android.support.test.*
import android.support.test.runner.*
import android.view.*
import android.widget.*
import org.junit.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class ViewGroupStyleApplierTest {

    private val context = InstrumentationRegistry.getTargetContext()!!
    private lateinit var view: ViewGroup
    private lateinit var styleApplier: ViewGroupStyleApplier
    private lateinit var styleBuilder: ViewGroupStyleApplier.StyleBuilder

    @Before
    fun setup() {
        view = FrameLayout(context)
        styleApplier = ViewGroupStyleApplier(view)
        styleBuilder = ViewGroupStyleApplier.StyleBuilder()
    }

    @Test
    fun auto() {
        for (mapping in (VIEW_MAPPINGS + VIEW_GROUP_MAPPINGS)) {
            mapping as BaseViewMapping<Any, *, ViewGroup, Any>

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
