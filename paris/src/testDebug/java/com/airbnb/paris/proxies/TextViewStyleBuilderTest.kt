package com.airbnb.paris.proxies

import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import android.view.View
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
    fun hint() {
        val style = builder.hint("This is a hint").build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.hint, "This is a hint")
                .build(),
            style
        )
    }

    @Test
    fun hintRes() {
        val style = builder.hintRes(R.string.test_arbitrary).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.hint, ResourceId(R.string.test_arbitrary))
                .build(),
            style
        )
    }

    @Test
    fun inputType() {
        val style = builder.inputType(InputType.TYPE_CLASS_DATETIME).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.inputType, InputType.TYPE_CLASS_DATETIME)
                .build(),
            style
        )
    }

    @Test
    fun inputTypeRes() {
        val style = builder.inputTypeRes(R.integer.test_arbitrary).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.inputType, ResourceId(R.integer.test_arbitrary))
                .build(),
            style
        )
    }

    @Test
    fun maxWidth() {
        val style = builder.maxWidth(100).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.maxWidth, 100)
                .build(),
            style
        )
    }

    @Test
    fun textColor_null() {
        val style = builder.textColor(null).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.textColor, null)
                .build(),
            style
        )
    }

    @Test
    fun textStyle() {
        val style = builder.textStyle(Typeface.NORMAL).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.textStyle, Typeface.NORMAL)
                .build(),
            style
        )
    }

    @Test
    fun textStyleRes() {
        val style = builder.textStyleRes(R.integer.test_arbitrary).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.textStyle, ResourceId(R.integer.test_arbitrary))
                .build(),
            style
        )
    }

    @Test
    fun visibility_invisible() {
        // View attributes are also available
        val style = builder.visibility(View.INVISIBLE).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.visibility, View.INVISIBLE)
                .build(),
            style
        )
    }
}
