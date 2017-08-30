package com.airbnb.paris

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.view.View
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StyleApplierTest {

    private open class TestStyleApplier(view: View) : StyleApplier<View, View>(view)

    private lateinit var context: Context
    private lateinit var view: View
    private lateinit var applier : TestStyleApplier;

    private fun newView() = View(context)

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        view = View(context)
        applier = TestStyleApplier(view)
    }

    @Test
    fun equals_empty_sameView() {
        val view = newView()
        assertEquals(TestStyleApplier(view), TestStyleApplier(view))
    }

    @Test
    fun equals_empty_differentViews() {
        assertNotEquals(TestStyleApplier(newView()), TestStyleApplier(newView()))
    }

    @Test
    fun nullAttributeSet() {
        // Applying a null AttributeSet should still call processAttributes(...) so that default
        // values can be applied even when views are created programmatically

        var methodCallCount = 0
        var a_: TypedArrayWrapper? = null
        val applier = object : TestStyleApplier(view) {
            override fun attributes(): IntArray? = intArrayOf(1, 2, 3)

            override fun processAttributes(style: Style, a: TypedArrayWrapper) {
                methodCallCount++
                a_ = a
            }
        }
        applier.apply(null)

        assertEquals(1, methodCallCount)
        assertEquals(0, a_!!.getIndexCount())
        Assert.assertFalse(a_!!.hasValue(42))
    }
}
