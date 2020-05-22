package com.airbnb.paris.proxies

import android.widget.ImageView
import android.widget.ImageViewStyleApplier
import android.widget.ImageViewStyleApplier.StyleBuilder
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageViewStyleApplierTest {

    private val context = InstrumentationRegistry.getTargetContext()!!
    private lateinit var view: ImageView
    private lateinit var styleApplier: ImageViewStyleApplier
    private lateinit var styleBuilder: StyleBuilder

    @Before
    fun setup() {
        view = ImageView(context)
        styleApplier = ImageViewStyleApplier(view)
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
