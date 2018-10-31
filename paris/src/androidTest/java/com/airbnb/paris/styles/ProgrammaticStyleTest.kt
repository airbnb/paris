package com.airbnb.paris.styles

import android.graphics.Color
import androidx.test.runner.AndroidJUnit4
import com.airbnb.paris.test.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProgrammaticStyleTest {

    @Test
    fun equals() {
        assertEquals(ProgrammaticStyle.builder().build(), ProgrammaticStyle.builder().build())
        assertEquals(
                ProgrammaticStyle.builder()
                        .putRes(R.attr.formatBoolean, R.bool.format_boolean)
                        .put(R.attr.formatColor, Color.GREEN)
                        .build(),
                ProgrammaticStyle.builder()
                        .putRes(R.attr.formatBoolean, R.bool.format_boolean)
                        .put(R.attr.formatColor, Color.GREEN)
                        .build()
        )
        assertNotEquals(
                ProgrammaticStyle.builder()
                        .putRes(R.attr.formatBoolean, R.bool.format_boolean)
                        .put(R.attr.formatColor, Color.GREEN)
                        .build(),
                ProgrammaticStyle.builder()
                        .putRes(R.attr.formatBoolean, R.bool.format_boolean_2)
                        .put(R.attr.formatColor, Color.GREEN)
                        .build()
        )
        assertNotEquals(
                ProgrammaticStyle.builder()
                        .putRes(R.attr.formatBoolean, R.bool.format_boolean)
                        .put(R.attr.formatColor, Color.GREEN)
                        .build(),
                ProgrammaticStyle.builder()
                        .putRes(R.attr.formatBoolean, R.bool.format_boolean_2)
                        .build()
        )
    }
}
