package com.airbnb.paris.views.java

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
class WithStyleableChildExtensionsTest {

    private lateinit var context: Context
    private lateinit var withStyleableChildView: WithStyleableChildView

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        withStyleableChildView = WithStyleableChildView(context)
    }

    @Test
    fun style_builderDefault() {
        // Tests that the extension to set a default style exists
        withStyleableChildView.style { addDefault() }
    }

    @Test
    fun style_builderSubStyleStyle() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withStyleableChildView.style {
            testArbitraryStyle(viewStyle {
                visibility(View.INVISIBLE)
            })
        }
        assertEquals(withStyleableChildView.arbitrarySubView.visibility, View.INVISIBLE)
    }

    @Test
    fun style_builderSubStyleStyleRes() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withStyleableChildView.style {
            testArbitraryStyle(R.style.Test_WithStyleableChildViewStyleExtensions_Style_BuilderSubStyleStyle_Invisible)
        }
        assertEquals(withStyleableChildView.arbitrarySubView.visibility, View.INVISIBLE)
    }

    @Test
    fun style_builderSubStyleStyleBuilder() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withStyleableChildView.style {
            testArbitraryStyle {
                visibility(View.INVISIBLE)
            }
        }
        assertEquals(withStyleableChildView.arbitrarySubView.visibility, View.INVISIBLE)
    }
}
