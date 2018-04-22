package com.airbnb.paris.proxies

import android.content.Context
import android.view.View
import com.airbnb.paris.R
import com.airbnb.paris.WithSubStyleView
import com.airbnb.paris.extensions.style
import com.airbnb.paris.extensions.testArbitraryStyle
import com.airbnb.paris.extensions.viewStyle
import com.airbnb.paris.extensions.visibility
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WithSubStyleViewStyleExtensionsTest {

    private lateinit var context: Context
    private lateinit var withSubStyleView: WithSubStyleView

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        withSubStyleView = WithSubStyleView(context)
    }

    @Test
    fun style_builderSubStyleStyle() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withSubStyleView.style {
            testArbitraryStyle(viewStyle {
                visibility(View.INVISIBLE)
            })
        }
        assertEquals(withSubStyleView.arbitrarySubView.visibility, View.INVISIBLE)
    }

    @Test
    fun style_builderSubStyleStyleRes() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withSubStyleView.style {
            testArbitraryStyle(R.style.Test_WithSubStyleViewStyleExtensions_Style_BuilderSubStyleStyle_Invisible)
        }
        assertEquals(withSubStyleView.arbitrarySubView.visibility, View.INVISIBLE)
    }

    @Test
    fun style_builderSubStyleStyleBuilder() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withSubStyleView.style {
            testArbitraryStyle {
                visibility(View.INVISIBLE)
            }
        }
        assertEquals(withSubStyleView.arbitrarySubView.visibility, View.INVISIBLE)
    }
}
