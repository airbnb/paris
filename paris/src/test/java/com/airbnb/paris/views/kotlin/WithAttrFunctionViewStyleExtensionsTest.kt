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
class WithAttrFunctionViewStyleExtensionsTest {

    private lateinit var context: Context
    private lateinit var withAttrFunctionView: WithAttrFunctionView

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        withAttrFunctionView = WithAttrFunctionView(context)
    }

    @Test
    fun style_builderAttr() {
        // Tests that the extension to build and set an attribute exists and works
        withAttrFunctionView.arbitraryBooleanValue = false
        withAttrFunctionView.style {
            testArbitraryBoolean(true)
        }
        assertEquals(true, withAttrFunctionView.arbitraryBooleanValue)
    }

    @Test
    fun style_builderAttrRes() {
        // Tests that the extension to build and set an attribute exists and works
        withAttrFunctionView.arbitraryBooleanValue = false
        withAttrFunctionView.style {
            testArbitraryBooleanRes(R.bool.test_true)
        }
        assertEquals(true, withAttrFunctionView.arbitraryBooleanValue)
    }
}
