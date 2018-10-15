package com.airbnb.paris.views.kotlin

import android.content.Context
import android.view.View
import com.airbnb.paris.extensions.style
import com.airbnb.paris.extensions.testArbitraryStyle
import com.airbnb.paris.extensions.visibility
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WithStyleableChildInternalViewStyleExtensionsTest {

    private lateinit var context: Context
    private lateinit var withStyleableChildInternalView: WithStyleableChildInternalView

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        withStyleableChildInternalView = WithStyleableChildInternalView(context)
    }

    @Test
    fun style_builderSubStyleStyleBuilder() {
        // Tests that the extension to build and set a sub-style exist and work with an arbitrary
        // attribute
        withStyleableChildInternalView.style {
            testArbitraryStyle {
                visibility(View.INVISIBLE)
            }
        }
        assertEquals(withStyleableChildInternalView.arbitrarySubView.visibility, View.INVISIBLE)
    }
}
