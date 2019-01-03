package com.airbnb.paris.proxies

import android.R
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayoutStyleApplier
import com.airbnb.paris.extensions.addDefault
import com.airbnb.paris.extensions.layoutWeight
import com.airbnb.paris.extensions.style
import com.airbnb.paris.extensions.visibility
import com.airbnb.paris.styles.ProgrammaticStyle
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class LinearLayoutStyleExtensionsTest {

    private lateinit var context: Context
    private lateinit var linearLayout: LinearLayout
    private lateinit var builder: LinearLayoutStyleApplier.StyleBuilder

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        linearLayout = LinearLayout(context)
        linearLayout.layoutParams = LinearLayout.LayoutParams(100, 100)
        builder = LinearLayoutStyleApplier.StyleBuilder()
    }

    @Test
    fun style_styleStyle() {
        val weight = 0.78f
        // Tests that the extension to set a style resource exists and works with an arbitrary
        // attribute
        linearLayout.style(builder.layoutWeight(weight).build())
        Assert.assertEquals(weight, (linearLayout.layoutParams as LinearLayout.LayoutParams).weight)
    }

    @Test
    fun style_styleNullAttributeSet() {
        // Tests that setting a null AttributeSet is possible
        linearLayout.style(null)
    }

    @Test
    fun style_builder() {
        val weight = 0.8f
        // Tests that the extension to build and set a style exists and works with an arbitrary
        // attribute
        linearLayout.style {
            layoutWeight(weight)
        }
        Assert.assertEquals(weight, (linearLayout.layoutParams as LinearLayout.LayoutParams).weight)
    }

    @Test
    fun style_builderDefault() {
        // Tests that the extension to set a default style exists
        linearLayout.style { addDefault() }
    }

    @Test
    fun viewStyle() {
        val weight = 0.9f
        // Tests that the function to create a view style exists and works with an arbitrary
        // attribute
        val style = com.airbnb.paris.extensions.linearLayoutStyle {
            layoutWeight(weight)
        }
        assertEquals(
            style,
            ProgrammaticStyle.builder()
                .put(R.attr.layout_weight, weight)
                .build()
        )
    }
}
