package com.airbnb.paris

import android.support.test.runner.AndroidJUnit4
import android.view.View
import com.airbnb.paris.test.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
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
                builder1.add(R.style.Green),
                builder2.add(R.style.Green)
        )
    }

    @Test
    fun equals_styleRes_different() {
        assertNotEquals(
                builder1.add(R.style.Green),
                builder2.add(R.style.Red)
        )
    }
}
