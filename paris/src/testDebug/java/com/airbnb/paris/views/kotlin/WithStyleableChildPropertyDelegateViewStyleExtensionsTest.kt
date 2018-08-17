package com.airbnb.paris.views.kotlin

import android.content.Context
import android.view.View
import com.airbnb.paris.R
import com.airbnb.paris.extensions.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WithStyleableChildPropertyDelegateViewStyleExtensionsTest {

    private lateinit var context: Context
    private lateinit var withStyleableChildPropertyDelegateView: WithStyleableChildPropertyDelegateView

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        withStyleableChildPropertyDelegateView = WithStyleableChildPropertyDelegateView(context)
    }

    @Test
    fun style_builderDefault() {
        // Tests that the extension to set a default style exists
        withStyleableChildPropertyDelegateView.style { addDefault() }
    }

    @Test
    fun style_builderSubStyleStyle() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withStyleableChildPropertyDelegateView.style {
            testArbitraryStyle(viewStyle {
                visibility(View.INVISIBLE)
            })
        }
        assertEquals(withStyleableChildPropertyDelegateView.arbitrarySubView.visibility, View.INVISIBLE)
    }

    @Test
    fun style_builderSubStyleStyleRes() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withStyleableChildPropertyDelegateView.style {
            testArbitraryStyle(R.style.Test_WithStyleableChildPropertyDelegateViewStyleExtensions_Style_BuilderSubStyleStyle_Invisible)
        }
        assertEquals(withStyleableChildPropertyDelegateView.arbitrarySubView.visibility, View.INVISIBLE)
    }

    @Test
    fun style_builderSubStyleStyleBuilder() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withStyleableChildPropertyDelegateView.style {
            testArbitraryStyle {
                visibility(View.INVISIBLE)
            }
        }
        assertEquals(withStyleableChildPropertyDelegateView.arbitrarySubView.visibility, View.INVISIBLE)
    }

    @Test
    fun withStyleableChildPropertyDelegateViewStyle() {
        // Tests that the extension to build and set a style exists and works with an arbitrary attribute.
        withStyleableChildPropertyDelegateView.arbitrarySubView.visibility = View.VISIBLE
        withStyleableChildPropertyDelegateView.style(withStyleableChildKotlinViewStyle {
            testArbitraryStyle {
                visibility(View.INVISIBLE)
            }
        })
        assertEquals(withStyleableChildPropertyDelegateView.arbitrarySubView.visibility, View.INVISIBLE)
    }
}
