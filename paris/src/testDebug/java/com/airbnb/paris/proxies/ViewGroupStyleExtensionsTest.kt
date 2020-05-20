package com.airbnb.paris.proxies

import android.content.Context
import android.view.View
import android.view.ViewGroupStyleApplier
import android.widget.LinearLayout
import com.airbnb.paris.R
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
class ViewGroupStyleExtensionsTest {

    private lateinit var context: Context
    private lateinit var viewGroup: LinearLayout
    private lateinit var builder: ViewGroupStyleApplier.StyleBuilder

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        viewGroup = LinearLayout(context)
        builder = ViewGroupStyleApplier.StyleBuilder()
    }

    @Test
    fun style_styleStyle() {
        // Tests that the extension to set a style resource exists and works with an arbitrary
        // attribute
        viewGroup.style(builder.visibility(View.INVISIBLE).build())
        assertEquals(viewGroup.visibility, View.INVISIBLE)
    }

    @Test
    fun style_styleRes() {
        // Tests that the extension to set a style resource exists and works with an arbitrary
        // attribute
        viewGroup.style(R.style.Test_ViewGroupStyleExtensions_Style_Invisible)
        assertEquals(viewGroup.visibility, View.INVISIBLE)
    }

    @Test
    fun style_styleNullAttributeSet() {
        // Tests that setting a null AttributeSet is possible
        viewGroup.style(null)
    }

    @Test
    fun style_builder() {
        // Tests that the extension to build and set a style exists and works with an arbitrary
        // attribute
        viewGroup.style {
            visibility(View.INVISIBLE)
        }
        assertEquals(viewGroup.visibility, View.INVISIBLE)
    }

    @Test
    fun viewStyle() {
        // Tests that the function to create a viewGroup style exists and works with an arbitrary
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
