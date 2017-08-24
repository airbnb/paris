package com.airbnb.paris

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StyleApplierTest {

    private class TestStyleApplier(view: View) : StyleApplier<View, View>(view)

    private lateinit var context: Context

    private fun newView() = View(context)

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
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
}