package com.airbnb.paris.views.kotlin

import android.content.Context
import android.view.View
import com.airbnb.paris.R
import com.airbnb.paris.extensions.addDefault
import com.airbnb.paris.extensions.style
import com.airbnb.paris.extensions.testArbitraryStyle
import com.airbnb.paris.extensions.viewStyle
import com.airbnb.paris.extensions.visibility
import com.airbnb.paris.extensions.withStyleableChildKotlinViewStyle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WithStyleableChildKotlinViewStyleExtensionsTest {

    private lateinit var context: Context
    private lateinit var withStyleableChildKotlinView: WithStyleableChildKotlinView

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        withStyleableChildKotlinView = WithStyleableChildKotlinView(context)
    }

    @Test
    fun style_builderDefault() {
        // Tests that the extension to set a default style exists
        withStyleableChildKotlinView.style { addDefault() }
    }

    @Test
    fun style_builderSubStyleStyle() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withStyleableChildKotlinView.style {
            testArbitraryStyle(viewStyle {
                visibility(View.INVISIBLE)
            })
        }
        assertEquals(withStyleableChildKotlinView.arbitrarySubView.visibility, View.INVISIBLE)
    }

    @Test
    fun style_builderSubStyleStyleRes() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withStyleableChildKotlinView.style {
            testArbitraryStyle(R.style.Test_WithStyleableChildKotlinViewStyleExtensions_Style_BuilderSubStyleStyle_Invisible)
        }
        assertEquals(withStyleableChildKotlinView.arbitrarySubView.visibility, View.INVISIBLE)
    }

    @Test
    fun style_builderSubStyleStyleBuilder() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withStyleableChildKotlinView.style {
            testArbitraryStyle {
                visibility(View.INVISIBLE)
            }
        }
        assertEquals(withStyleableChildKotlinView.arbitrarySubView.visibility, View.INVISIBLE)
    }

    @Test
    fun withStyleableChildKotlinViewStyle() {
        // Tests that the extension to build and set a style exists and works with an arbitrary attribute.
        withStyleableChildKotlinView.arbitrarySubView.visibility = View.VISIBLE
        withStyleableChildKotlinView.style(withStyleableChildKotlinViewStyle {
            testArbitraryStyle {
                visibility(View.INVISIBLE)
            }
        })
        assertEquals(withStyleableChildKotlinView.arbitrarySubView.visibility, View.INVISIBLE)
    }
}
