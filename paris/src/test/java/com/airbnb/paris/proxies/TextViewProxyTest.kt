package com.airbnb.paris.proxies

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
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
    fun setHint_normal() {
        proxy.setHint("This is a hint")
        assertEquals("This is a hint", view.hint)
    }

    @Test
    fun setHint_null() {
        // Since null is the default first set the hint to something else
        view.hint = "This is a hint"
        proxy.setHint(null)
        assertEquals(null, view.hint)
    }

    @Test
    fun setInputType_classDatetime() {
        proxy.setInputType(InputType.TYPE_CLASS_DATETIME)
        assertEquals(InputType.TYPE_CLASS_DATETIME, view.inputType)
    }

    @Test
    fun setInputType_classNumber() {
        proxy.setInputType(InputType.TYPE_CLASS_NUMBER)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.inputType)
    }

    @Test
    fun setInputType_classPhone() {
        proxy.setInputType(InputType.TYPE_CLASS_PHONE)
        assertEquals(InputType.TYPE_CLASS_PHONE, view.inputType)
    }

    @Test
    fun setInputType_classText() {
        proxy.setInputType(InputType.TYPE_CLASS_TEXT)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.inputType)
    }

    @Test
    fun setInputType_classTextVariationPassword() {
        proxy.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD, view.inputType)
    }

    @Test
    fun setInputType_classTextVariationUri() {
        proxy.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI)
        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI, view.inputType)
    }

    @Test
    fun setInputType_classTextVariationAutocomplete() {
        proxy.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE)
        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE, view.inputType)
    }

    @Test
    fun setTextColor_null() {
        proxy.setTextColor(null)
        assertEquals(ColorStateList.valueOf(0xFF000000.toInt()), view.textColors)
    }

    @Test
    fun setTextColorHint_null() {
        // Since normal is the default first set the color to something else
        view.setHintTextColor(Color.WHITE)
        proxy.setTextColorHint(null)
        assertEquals(null, view.hintTextColors)
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
