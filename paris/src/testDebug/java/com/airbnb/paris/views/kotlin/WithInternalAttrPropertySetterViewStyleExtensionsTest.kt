package com.airbnb.paris.views.kotlin

import android.content.Context
import com.airbnb.paris.extensions.style
import com.airbnb.paris.extensions.testArbitraryBoolean
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WithInternalAttrPropertySetterViewStyleExtensionsTest {

    private lateinit var context: Context
    private lateinit var withInternalAttrPropertySetterView: WithInternalAttrPropertySetterView

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        withInternalAttrPropertySetterView = WithInternalAttrPropertySetterView(context)
    }

    @Test
    fun style_builderAttr() {
        // Tests that the extension to build and set an attribute exists and works.
        withInternalAttrPropertySetterView.arbitraryBoolean = false
        withInternalAttrPropertySetterView.style {
            testArbitraryBoolean(true)
        }
        assertEquals(true, withInternalAttrPropertySetterView.arbitraryBoolean)
    }
}
