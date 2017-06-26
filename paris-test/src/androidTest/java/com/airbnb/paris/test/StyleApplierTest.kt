package com.airbnb.paris.test

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.view.View
import com.airbnb.paris.Style
import com.airbnb.paris.StyleApplier
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StyleApplierTest {

    private class TestStyleApplier(view: View) : StyleApplier<TestStyleApplier, View>(view)

    private lateinit var context: Context
    private lateinit var view: View
    private lateinit var applier : TestStyleApplier;

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        view = View(context)
        applier = TestStyleApplier(view)
    }

    @Test
    fun onStyleApply() {
        var styleReceived: Style? = null
        applier.onStyleApply = {
            styleReceived = it
        }
        val style = Style(666)
        applier.apply(style)
        assertEquals(style, styleReceived)
    }

    @Test(expected = IllegalStateException::class)
    fun onStyleApply_protectedSetter() {
        // This setter isn't meant to be used publicly so we throw an exception if it's set more
        // than once (this way it cannot be unset)

        applier.onStyleApply = {}
        applier.onStyleApply = {}
    }
}
