package com.airbnb.paris.proxies

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.widget.TextView
import com.airbnb.paris.R
import com.airbnb.paris.utils.assertTypefaceEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    fun setInputType_null() {
        proxy.setInputType(InputType.TYPE_NULL)
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertEquals(InputType.TYPE_NULL, view.inputType)
    }

    @Test
    fun setInputType_classDatetime() {
        proxy.setInputType(InputType.TYPE_CLASS_DATETIME)
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertEquals(InputType.TYPE_CLASS_DATETIME, view.inputType)
    }

    @Test
    fun setInputType_classNumber() {
        proxy.setInputType(InputType.TYPE_CLASS_NUMBER)
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.inputType)
    }

    @Test
    fun setInputType_classPhone() {
        proxy.setInputType(InputType.TYPE_CLASS_PHONE)
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertEquals(InputType.TYPE_CLASS_PHONE, view.inputType)
    }

    @Test
    fun setInputType_classText() {
        proxy.setInputType(InputType.TYPE_CLASS_TEXT)
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.inputType)
    }

    @Test
    fun setInputType_classTextVariationPassword() {
        proxy.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertEquals(
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            view.inputType
        )
    }

    @Test
    fun setInputType_classTextVariationUri() {
        proxy.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI)
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI, view.inputType)
    }

    @Test
    fun setInputType_classTextVariationAutocomplete() {
        proxy.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE)
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertEquals(
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE,
            view.inputType
        )
    }

    @Test
    fun setMaxWidth() {
        view.maxWidth = 0
        proxy.setMaxWidth(100)
        assertEquals(100, view.maxWidth)
    }

    @Test
    fun setSingleLine_true() {
        view.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        proxy.setSingleLine(true)
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertTrue(view.inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE == 0)
    }

    @Test
    fun setSingleLine_false() {
        view.inputType = InputType.TYPE_CLASS_TEXT
        proxy.setSingleLine(false)
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertEquals(
            InputType.TYPE_TEXT_FLAG_MULTI_LINE,
            view.inputType and InputType.TYPE_MASK_FLAGS
        )
    }

    @Test
    fun setTextAppearance_textSize() {
        view.textSize = 24.toFloat()

        proxy.setTextAppearance(R.style.Base_TextAppearance_AppCompat_Medium)
        assertEquals(view.textSize, view.resources.getDimension(R.dimen.abc_text_size_medium_material))
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
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertEquals(Typeface.NORMAL, view.typeface.style)
    }

    @Test
    fun setTextStyle_bold() {
        proxy.setTextStyle(Typeface.BOLD)
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertEquals(Typeface.BOLD, view.typeface.style)
    }

    @Test
    fun setTextStyle_italic() {
        proxy.setTextStyle(Typeface.ITALIC)
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertEquals(Typeface.ITALIC, view.typeface.style)
    }

    @Test
    fun setTextStyle_boldItalic() {
        proxy.setTextStyle(Typeface.BOLD_ITALIC)
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertEquals(Typeface.BOLD_ITALIC, view.typeface.style)
    }

    @Test
    fun setFontFamily_sansSerif_normal() {
        // Set typeface to sans-serif-bold since sans-serif is default one
        view.typeface = Typeface.create("sans-serif-bold", Typeface.NORMAL)
        proxy.setFontFamily(Typeface.create("sans-serif", Typeface.NORMAL))
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertTypefaceEquals(Typeface.create("sans-serif", Typeface.NORMAL), view.typeface)
    }

    @Test
    fun setFontFamily_sansSerif_boldStyle() {
        proxy.setFontFamily(Typeface.create("sans-serif", Typeface.BOLD))
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertTypefaceEquals(Typeface.create("sans-serif", Typeface.BOLD), view.typeface)
    }

    @Test
    fun setFontFamily_sansSerif_boldTextStyle() {
        proxy.setFontFamily(Typeface.create("sans-serif", Typeface.NORMAL))
        proxy.setTextStyle(Typeface.BOLD)
        // IMPLEMENTATION DETAIL: the style isn't needed
        proxy.afterStyle(null)
        assertTypefaceEquals(Typeface.create("sans-serif", Typeface.BOLD), view.typeface)
    }

    @Test
    fun setDrawablePadding() {
        view.compoundDrawablePadding = 0
        proxy.setDrawablePadding(100)
        assertEquals(100, view.compoundDrawablePadding)
    }

    @Test
    fun setLineHeight() {
        val lineHeight = 18
        proxy.setLineHeight(lineHeight)

        assertEquals(lineHeight, view.lineHeight)
    }
}
