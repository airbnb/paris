package com.airbnb.paris.styles

import android.content.Context
import android.graphics.Color
import com.airbnb.paris.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ProgrammaticStyleTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
    }

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

    /**
     * If the style doesn't contain a given attribute but the theme does, we should default to it.
     */
    @Test
    fun styleableAttributeFromTheme() {
        context.setTheme(R.style.Theme_AppCompat)
        val emptyStyle = ProgrammaticStyle.builder().build()
        val ta = emptyStyle.obtainStyledAttributes(context, R.styleable.Paris_TextView)
        val actualTextAppearance = ta.getResourceId(R.styleable.Paris_TextView_android_textAppearance)
        assertEquals(android.R.style.TextAppearance_Material, actualTextAppearance)
    }
}
