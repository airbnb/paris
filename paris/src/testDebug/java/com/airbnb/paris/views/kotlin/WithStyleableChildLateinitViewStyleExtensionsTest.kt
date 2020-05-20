package com.airbnb.paris.views.kotlin

import android.content.Context
import android.view.View
import com.airbnb.paris.R
import com.airbnb.paris.extensions.addDefault
import com.airbnb.paris.extensions.style
import com.airbnb.paris.extensions.testArbitraryStyle
import com.airbnb.paris.extensions.viewStyle
import com.airbnb.paris.extensions.visibility
import com.airbnb.paris.extensions.withStyleableChildLateinitViewStyle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WithStyleableChildLateinitViewStyleExtensionsTest {

    private lateinit var context: Context
    private lateinit var withStyleableChildLateinitView: WithStyleableChildLateinitView

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        withStyleableChildLateinitView = WithStyleableChildLateinitView(context)
        withStyleableChildLateinitView.init()
    }

    @Test
    fun style_builderDefault() {
        // Tests that the extension to set a default style exists
        withStyleableChildLateinitView.style { addDefault() }
    }

    @Test
    fun style_builderSubStyleStyle() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withStyleableChildLateinitView.style {
            testArbitraryStyle(viewStyle {
                visibility(View.INVISIBLE)
            })
        }
        assertEquals(withStyleableChildLateinitView.arbitrarySubView.visibility, View.INVISIBLE)
    }

    @Test
    fun style_builderSubStyleStyleRes() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withStyleableChildLateinitView.style {
            testArbitraryStyle(R.style.Test_WithStyleableChildLateinitViewStyleExtensions_Style_BuilderSubStyleStyle_Invisible)
        }
        assertEquals(withStyleableChildLateinitView.arbitrarySubView.visibility, View.INVISIBLE)
    }

    @Test
    fun style_builderSubStyleStyleBuilder() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withStyleableChildLateinitView.style {
            testArbitraryStyle {
                visibility(View.INVISIBLE)
            }
        }
        assertEquals(withStyleableChildLateinitView.arbitrarySubView.visibility, View.INVISIBLE)
    }

    @Test
    fun withStyleableChildLateinitViewStyle() {
        // Tests that the extension to build and set a style exists and works with an arbitrary attribute.
        withStyleableChildLateinitView.arbitrarySubView.visibility = View.VISIBLE
        withStyleableChildLateinitView.style(withStyleableChildLateinitViewStyle {
            testArbitraryStyle {
                visibility(View.INVISIBLE)
            }
        })
        assertEquals(withStyleableChildLateinitView.arbitrarySubView.visibility, View.INVISIBLE)
    }
}
