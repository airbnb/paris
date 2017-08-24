package com.airbnb.paris

import android.graphics.Color
import android.support.test.runner.AndroidJUnit4
import com.airbnb.paris.test.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SimpleStyleTest {

    @Test
    fun equals_styleRes() {
        assertEquals(SimpleStyle(R.style.Red), SimpleStyle(R.style.Red))
        assertNotEquals(SimpleStyle(R.style.Red), SimpleStyle(R.style.Green))
    }

    @Test
    fun equals_styleBuilder() {
        assertEquals(SimpleStyle.builder().build(), SimpleStyle.builder().build())
        assertEquals(
                SimpleStyle.builder()
                        .putRes(R.attr.formatBoolean, R.bool.format_boolean)
                        .put(R.attr.formatColor, Color.GREEN)
                        .build(),
                SimpleStyle.builder()
                        .putRes(R.attr.formatBoolean, R.bool.format_boolean)
                        .put(R.attr.formatColor, Color.GREEN)
                        .build()
        )
        assertNotEquals(
                SimpleStyle.builder()
                        .putRes(R.attr.formatBoolean, R.bool.format_boolean)
                        .put(R.attr.formatColor, Color.GREEN)
                        .build(),
                SimpleStyle.builder()
                        .putRes(R.attr.formatBoolean, R.bool.format_boolean_2)
                        .put(R.attr.formatColor, Color.GREEN)
                        .build()
        )
        assertNotEquals(
                SimpleStyle.builder()
                        .putRes(R.attr.formatBoolean, R.bool.format_boolean)
                        .put(R.attr.formatColor, Color.GREEN)
                        .build(),
                SimpleStyle.builder()
                        .putRes(R.attr.formatBoolean, R.bool.format_boolean_2)
                        .build()
        )
    }

    @Test
    fun equals_mix() {
        assertNotEquals(SimpleStyle(R.style.Red), SimpleStyle.builder().build())
    }
}
