package com.airbnb.paris.views.kotlin

import android.content.Context
import com.airbnb.paris.R
import com.airbnb.paris.extensions.style
import com.airbnb.paris.extensions.testArbitraryBoolean
import com.airbnb.paris.extensions.testArbitraryBooleanRes
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WithAttrPropertySetterViewStyleExtensionsTest {

    private lateinit var context: Context
    private lateinit var withAttrPropertySetterView: WithAttrPropertySetterView

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        withAttrPropertySetterView = WithAttrPropertySetterView(context)
    }

    @Test
    fun style_builderAttr() {
        // Tests that the extension to build and set an attribute exists and works
        withAttrPropertySetterView.arbitraryBoolean = false
        withAttrPropertySetterView.style {
            testArbitraryBoolean(true)
        }
        assertEquals(true, withAttrPropertySetterView.arbitraryBoolean)
    }

    @Test
    fun style_builderAttrRes() {
        // Tests that the extension to build and set an attribute exists and works
        withAttrPropertySetterView.arbitraryBoolean = false
        withAttrPropertySetterView.style {
            testArbitraryBooleanRes(R.bool.test_true)
        }
        assertEquals(true, withAttrPropertySetterView.arbitraryBoolean)
    }
}
