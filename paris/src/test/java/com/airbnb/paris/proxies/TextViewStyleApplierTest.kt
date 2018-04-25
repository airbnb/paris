package com.airbnb.paris.proxies

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.InputType
import android.view.View
import android.widget.TextView
import android.widget.TextViewStyleApplier
import com.airbnb.paris.R
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
    fun hint_normal() {
        applier.apply(builder.hint("This is a hint").build())
        assertEquals("This is a hint", view.hint)
    }

    // TODO Re-enable once null values are supported
    //@Test
    fun hint_null() {
        // Since null is the default first set the hint to something else
        view.hint = "This is a hint"
        applier.apply(builder.hint(null).build())
        assertEquals(null, view.hint)
    }

    @Test
    fun inputType_classDatetimeProgrammatic() {
        applier.apply(builder.inputType(InputType.TYPE_CLASS_DATETIME).build())
        assertEquals(InputType.TYPE_CLASS_DATETIME, view.inputType)
    }

    @Test
    fun inputType_classDatetimeXml() {
        applier.apply(R.style.Test_TextViewStyleApplier_InputType_ClassDatetime)
        assertEquals(InputType.TYPE_CLASS_DATETIME, view.inputType)
    }

    @Test
    fun inputType_classNumberXml() {
        applier.apply(R.style.Test_TextViewStyleApplier_InputType_ClassNumber)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.inputType)
    }

    @Test
    fun inputType_classPhoneXml() {
        applier.apply(R.style.Test_TextViewStyleApplier_InputType_ClassPhone)
        assertEquals(InputType.TYPE_CLASS_PHONE, view.inputType)
    }

    @Test
    fun inputType_classTextXml() {
        applier.apply(R.style.Test_TextViewStyleApplier_InputType_ClassText)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.inputType)
    }

    @Test
    fun inputType_classTextVariationPasswordXml() {
        applier.apply(R.style.Test_TextViewStyleApplier_InputType_ClassTextVariationPassword)
        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD, view.inputType)
    }

    @Test
    fun inputType_classTextVariationUriXml() {
        applier.apply(R.style.Test_TextViewStyleApplier_InputType_ClassTextVariationUri)
        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI, view.inputType)
    }

    @Test
    fun inputType_classTextFlagAutoCompleteXml() {
        applier.apply(R.style.Test_TextViewStyleApplier_InputType_ClassTextFlagAutoComplete)
        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE, view.inputType)
    }

    @Test
    fun textColor_null() {
        applier.apply(builder.textColor(null).build())
        assertEquals(ColorStateList.valueOf(0xFF000000.toInt()), view.textColors)
    }

    @Test
    fun textColor_nullRes() {
        applier.apply(builder.textColorRes(R.color.null_).build())
        assertEquals(ColorStateList.valueOf(0xFF000000.toInt()), view.textColors)
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

    @Test
    fun visibility_invisible() {
        // View attributes are also available
        applier.apply(builder.visibility(View.INVISIBLE).build())
        assertEquals(View.INVISIBLE, view.visibility)
    }

}
