package com.airbnb.paris.test

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.view.View
import com.airbnb.paris.Style
import com.airbnb.paris.StyleApplier
import com.airbnb.paris.TypedArrayWrapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StyleApplierTest {

    private open class TestStyleApplier(view: View) : StyleApplier<TestStyleApplier, View>(view) {
        override fun attributes(): IntArray? {
            return intArrayOf(1, 2, 3)
        }
    }

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

    @Test
    fun nullAttributeSet() {
        // Applying a null AttributeSet should still call processAttributes(...) so that default
        // values can be applied even when views are created programmatically

        var methodCallCount = 0
        var style_: Style? = null
        var a_: TypedArrayWrapper? = null
        val applier = object : TestStyleApplier(view) {
            override fun processAttributes(style: Style, a: TypedArrayWrapper) {
                methodCallCount++
                style_ = style
                a_ = a
            }
        }
        applier.apply(null)

        assertEquals(1, methodCallCount)
        assertEquals(0, a_!!.getIndexCount())
        assertFalse(a_!!.hasValue(42))
        assertEquals(null, style_!!.attributeSet)
        assertEquals(0, style_!!.styleRes)
        assertEquals(null, style_!!.config)
    }
}
