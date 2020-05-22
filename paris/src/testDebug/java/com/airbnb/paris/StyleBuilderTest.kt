package com.airbnb.paris

import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class StyleBuilderTest {

    private class TestStyleApplier(view: View) : StyleApplier<View, View>(view)
    private class TestStyleBuilder : StyleBuilder<TestStyleBuilder, TestStyleApplier>()

    private lateinit var builder1: TestStyleBuilder
    private lateinit var builder2: TestStyleBuilder

    @Before
    fun setup() {
        builder1 = TestStyleBuilder()
        builder2 = TestStyleBuilder()
    }

    @Test
    fun equals_empty() {
        assertEquals(builder1, builder2)
    }

    @Test
    fun equals_styleRes_same() {
        assertEquals(
            builder1.add(android.R.style.Widget_Button),
            builder2.add(android.R.style.Widget_Button)
        )
    }

    @Test
    fun equals_styleRes_different() {
        assertNotEquals(
            builder1.add(android.R.style.Widget_Button),
            builder2.add(android.R.style.Widget_Button_Small)
        )
    }
}
