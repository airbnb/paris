package com.airbnb.paris.proxies

import android.content.Context
import android.view.View
import android.view.ViewStyleApplier
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
class ViewStyleExtensionsTest {

    private lateinit var context: Context
    private lateinit var view: View
    private lateinit var builder: ViewStyleApplier.StyleBuilder

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        view = View(context)
        builder = ViewStyleApplier.StyleBuilder()
    }

    @Test
    fun style_styleStyle() {
        // Tests that the extension to set a style resource exists and works with an arbitrary
        // attribute
        view.style(builder.visibility(View.INVISIBLE).build())
        assertEquals(view.visibility, View.INVISIBLE)
    }

    @Test
    fun style_styleRes() {
        // Tests that the extension to set a style resource exists and works with an arbitrary
        // attribute
        view.style(R.style.Test_TextViewStyleExtensions_Style_Invisible)
        assertEquals(view.visibility, View.INVISIBLE)
    }

    @Test
    fun style_styleNullAttributeSet() {
        // Tests that setting a null AttributeSet is possible
        view.style(null)
    }

    @Test
    fun style_builder() {
        // Tests that the extension to build and set a style exists and works with an arbitrary
        // attribute
        view.style {
            visibility(View.INVISIBLE)
        }
        assertEquals(view.visibility, View.INVISIBLE)
    }

    @Test
    fun style_builderDefault() {
        // Tests that the extension to set a default style exists
        view.style { addDefault() }
    }

    @Test
    fun viewStyle() {
        // Tests that the function to create a view style exists and works with an arbitrary
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
