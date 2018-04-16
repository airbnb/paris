package com.airbnb.paris.proxies

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class TextViewProxyTest {

    private lateinit var context: Context
    private lateinit var view: TextView
    private lateinit var proxy: TextViewProxy

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        view = TextView(context)
        proxy = TextViewProxy(view)
    }

    @Test
    fun setTextStyle_normal() {
        // Since normal is the default first set the style to something else
        view.setTypeface(view.typeface, Typeface.BOLD)
        proxy.setTextStyle(Typeface.NORMAL)
        assertEquals(Typeface.NORMAL, view.typeface.style)
    }

    @Test
    fun setTextStyle_bold() {
        proxy.setTextStyle(Typeface.BOLD)
        assertEquals(Typeface.BOLD, view.typeface.style)
    }

    @Test
    fun setTextStyle_italic() {
        proxy.setTextStyle(Typeface.ITALIC)
        assertEquals(Typeface.ITALIC, view.typeface.style)
    }

    @Test
    fun setTextStyle_boldItalic() {
        proxy.setTextStyle(Typeface.BOLD_ITALIC)
        assertEquals(Typeface.BOLD_ITALIC, view.typeface.style)
    }
}
