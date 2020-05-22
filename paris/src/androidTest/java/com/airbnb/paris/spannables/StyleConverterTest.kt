package com.airbnb.paris.spannables

import android.graphics.Color
import android.graphics.Typeface
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.text.style.TypefaceSpan
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.airbnb.paris.attribute_values.ColorValue
import com.airbnb.paris.styles.ProgrammaticStyle
import com.airbnb.paris.styles.Style
import com.airbnb.paris.test.R
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.reflect.KClass

@RunWith(AndroidJUnit4::class)
class StyleConverterTest {

    private val converter = StyleConverter(InstrumentationRegistry.getTargetContext()!!)

    @Test
    fun textSize() {

        val bigTextStyle = ProgrammaticStyle.builder()
            .put(android.R.attr.textSize, 30)
            .build()

        assertStyleAppliesCorrectlyOnSampleString(bigTextStyle, AbsoluteSizeSpan::class) {
            assertThat(it.size, equalTo(30))
        }
    }

    @Test
    fun textColor() {

        val cyanTextStyle = ProgrammaticStyle.builder()
            .put(android.R.attr.textColor, ColorValue(Color.CYAN))
            .build()

        assertStyleAppliesCorrectlyOnSampleString(cyanTextStyle, ForegroundColorSpan::class) {
            assertThat(it.foregroundColor, equalTo(Color.CYAN))
        }
    }

    @Test
    fun textAppearance() {

        val cyanTextStyle = ProgrammaticStyle.builder()
            .put(android.R.attr.textAppearance, R.style.StyleConverterTest_MyTextAppearance)
            .build()

        assertStyleAppliesCorrectlyOnSampleString(cyanTextStyle, TextAppearanceSpan::class) {
            assertThat(it.textColor.defaultColor, equalTo(Color.GREEN))
            assertThat(it.textSize, equalTo(20))
        }
    }

    @Test
    fun fontFamily() {

        val cyanTextStyle = ProgrammaticStyle.builder()
            .put(android.R.attr.fontFamily, "monospace")
            .build()

        assertStyleAppliesCorrectlyOnSampleString(cyanTextStyle, TypefaceSpan::class) {
            assertThat(it.family, equalTo("monospace"))
        }
    }

    @Test
    fun typeFace() {

        val cyanTextStyle = ProgrammaticStyle.builder()
            .put(android.R.attr.typeface, "sans-serif")
            .build()

        assertStyleAppliesCorrectlyOnSampleString(cyanTextStyle, TypefaceSpan::class) {
            assertThat(it.family, equalTo("sans-serif"))
        }
    }

    @Test
    fun textStyle() {

        val cyanTextStyle = ProgrammaticStyle.builder()
            .put(android.R.attr.textStyle, Typeface.BOLD_ITALIC)
            .build()

        assertStyleAppliesCorrectlyOnSampleString(cyanTextStyle, StyleSpan::class) {
            assertThat(it.style, equalTo(Typeface.BOLD_ITALIC))
        }
    }

    private fun <T : Any> assertStyleAppliesCorrectlyOnSampleString(
        style: Style,
        expectedGeneratedSpanClass: KClass<T>,
        spanAssertions: ((T) -> Unit)
    ) {

        val sampleString = "Hello World"
        val spanned = converter.createSpannable(sampleString, setOf(StyleConverter.MarkupItem(IntRange(3, 5), style)))
        val spans = spanned.getSpans(0, sampleString.length, expectedGeneratedSpanClass.java)
        assertThat(spans.size, equalTo(1))

        val span = spans[0]
        spanAssertions(span)
        assertThat(spanned.getSpanStart(span), equalTo(3))
        assertThat(spanned.getSpanEnd(span), equalTo(5))
        assertThat(spanned.getSpans(0, 2, Object::class.java), equalTo(emptyArray()))
        assertThat(spanned.getSpans(6, 11, Object::class.java), equalTo(emptyArray()))

    }

}