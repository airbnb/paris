package com.airbnb.paris.proxies

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import android.widget.TextViewStyleApplier
import com.airbnb.paris.R
import com.airbnb.paris.attribute_values.ResourceId
import com.airbnb.paris.styles.ProgrammaticStyle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class TextViewStyleBuilderTest {

    private lateinit var context: Context
    private lateinit var view: TextView
    private lateinit var builder: TextViewStyleApplier.StyleBuilder

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        view = TextView(context)
        builder = TextViewStyleApplier.StyleBuilder()
    }

    @Test
    fun hint_programmatic() {
        val style = builder.hint("This is a hint").build()
        assertEquals(
                ProgrammaticStyle.builder()
                        .put(android.R.attr.hint, "This is a hint")
                        .build(),
                style
        )
    }

    @Test
    fun hint_xml() {
        val style = builder.hintRes(R.string.test_arbitrary).build()
        assertEquals(
                ProgrammaticStyle.builder()
                        .put(android.R.attr.hint, ResourceId(R.string.test_arbitrary))
                        .build(),
                style
        )
    }

    @Test
    fun textStyle_normal() {
        val style = builder.textStyle(Typeface.NORMAL).build()
        assertEquals(
                ProgrammaticStyle.builder()
                        .put(android.R.attr.textStyle, Typeface.NORMAL)
                        .build(),
                style
        )
    }

    @Test
    fun textStyle_bold() {
        val style = builder.textStyle(Typeface.BOLD).build()
        assertEquals(
                ProgrammaticStyle.builder()
                        .put(android.R.attr.textStyle, Typeface.BOLD)
                        .build(),
                style
        )
    }

    @Test
    fun textStyle_italic() {
        val style = builder.textStyle(Typeface.ITALIC).build()
        assertEquals(
                ProgrammaticStyle.builder()
                        .put(android.R.attr.textStyle, Typeface.ITALIC)
                        .build(),
                style
        )
    }

    @Test
    fun textStyle_boldItalic() {
        val style = builder.textStyle(Typeface.BOLD_ITALIC).build()
        assertEquals(
                ProgrammaticStyle.builder()
                        .put(android.R.attr.textStyle, Typeface.BOLD_ITALIC)
                        .build(),
                style
        )
    }
}
