package com.airbnb.paris.proxies

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ImageViewStyleApplier
import com.airbnb.paris.R
import com.airbnb.paris.extensions.addDefault
import com.airbnb.paris.extensions.style
import com.airbnb.paris.extensions.viewStyle
import com.airbnb.paris.extensions.visibility
import com.airbnb.paris.styles.ProgrammaticStyle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ImageViewStyleExtensionsTest {

    private lateinit var context: Context
    private lateinit var imageView: ImageView
    private lateinit var builder: ImageViewStyleApplier.StyleBuilder

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        imageView = ImageView(context)
        builder = ImageViewStyleApplier.StyleBuilder()
    }

    @Test
    fun style_styleStyle() {
        // Tests that the extension to set a style resource exists and works with an arbitrary
        // attribute
        imageView.style(builder.visibility(View.INVISIBLE).build())
        assertEquals(imageView.visibility, View.INVISIBLE)
    }

    @Test
    fun style_styleRes() {
        // Tests that the extension to set a style resource exists and works with an arbitrary
        // attribute
        imageView.style(R.style.Test_ImageViewStyleExtensions_Style_Invisible)
        assertEquals(imageView.visibility, View.INVISIBLE)
    }

    @Test
    fun style_styleNullAttributeSet() {
        // Tests that setting a null AttributeSet is possible
        imageView.style(null)
    }

    @Test
    fun style_builder() {
        // Tests that the extension to build and set a style exists and works with an arbitrary
        // attribute
        imageView.style {
            visibility(View.INVISIBLE)
        }
        assertEquals(imageView.visibility, View.INVISIBLE)
    }

    @Test
    fun style_builderDefault() {
        // Tests that the extension to set a default style exists
        imageView.style { addDefault() }
    }

    @Test
    fun viewStyle() {
        // Tests that the function to create a imageView style exists and works with an arbitrary
        // attribute
        val style = viewStyle {
            visibility(View.VISIBLE)
        }
        assertEquals(
                ProgrammaticStyle.builder()
                        .put(android.R.attr.visibility, View.VISIBLE)
                        .build(),
                style
        )
    }
}
