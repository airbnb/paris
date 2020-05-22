package com.airbnb.paris

import android.content.Context
import android.view.View
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.airbnb.paris.styles.Style
import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper
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
    private lateinit var applier: TestStyleApplier;

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
        // Applying a null AttributeSet should be a no-op

        var methodCallCount = 0
        val applier = object : TestStyleApplier(view) {
            override fun attributes(): IntArray? = intArrayOf(1, 2, 3)

            override fun processAttributes(style: Style, a: TypedArrayWrapper) {
                methodCallCount++
            }
        }
        applier.apply(null)

        assertEquals(0, methodCallCount)
    }
}
