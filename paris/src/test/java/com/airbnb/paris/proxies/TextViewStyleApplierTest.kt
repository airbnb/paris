package com.airbnb.paris.proxies

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import android.widget.TextViewStyleApplier
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class TextViewStyleApplierTest {

    private lateinit var context: Context
    private lateinit var view: TextView
    private lateinit var applier: TextViewStyleApplier
    private lateinit var builder: TextViewStyleApplier.StyleBuilder

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        view = TextView(context)
        applier = TextViewStyleApplier(view)
        builder = TextViewStyleApplier.StyleBuilder()
    }

    @Test
    fun textStyle_normal() {
        // Since normal is the default first set the style to something else
        view.setTypeface(view.typeface, Typeface.BOLD)
        applier.apply(builder.textStyle(Typeface.NORMAL).build())
        assertEquals(Typeface.NORMAL, view.typeface.style)
    }

    @Test
    fun textStyle_bold() {
        applier.apply(builder.textStyle(Typeface.BOLD).build())
        assertEquals(Typeface.BOLD, view.typeface.style)
    }

    @Test
    fun textStyle_italic() {
        applier.apply(builder.textStyle(Typeface.ITALIC).build())
        assertEquals(Typeface.ITALIC, view.typeface.style)
    }

    @Test
    fun textStyle_boldItalic() {
        applier.apply(builder.textStyle(Typeface.BOLD_ITALIC).build())
        assertEquals(Typeface.BOLD_ITALIC, view.typeface.style)
    }
}
