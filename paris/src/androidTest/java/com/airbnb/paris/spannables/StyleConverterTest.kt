package com.airbnb.paris.spannables

import android.graphics.Color
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import com.airbnb.paris.attribute_values.ColorValue
import com.airbnb.paris.styles.ProgrammaticStyle
import com.airbnb.paris.styles.Style
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.reflect.KClass

@RunWith(AndroidJUnit4::class)
class StyleConverterTest {

    private val converter = StyleConverter(InstrumentationRegistry.getTargetContext()!!)

    @Test
    fun textSize(){

        val bigTextStyle = ProgrammaticStyle.builder()
                .put(android.R.attr.textSize, 30)
                .build()

        assertStyleAppliesCorrectlyOnSampleString(bigTextStyle, AbsoluteSizeSpan::class) {
            assertThat(it.size, equalTo(30))
        }
    }

    @Test
    fun textColor(){

        val cyanTextStyle = ProgrammaticStyle.builder()
                .put(android.R.attr.textColor, ColorValue(Color.CYAN))
                .build()

        assertStyleAppliesCorrectlyOnSampleString(cyanTextStyle, ForegroundColorSpan::class) {
            assertThat(it.foregroundColor, equalTo(Color.CYAN))
        }
    }

    private fun <T: Any> assertStyleAppliesCorrectlyOnSampleString(
            style: Style,
            spanClass: KClass<T>,
            spanAssertions: ((T) -> Unit)) {

        val sampleString = "Hello World"
        val spanned = converter.createSpannable(sampleString, setOf(StyleConverter.MarkupItem(IntRange(3, 5), style)))
        val spans = spanned.getSpans(0, sampleString.length, spanClass.java)
        assertThat(spans.size, equalTo(1))

        val span = spans[0]
        spanAssertions(span)
        assertThat(spanned.getSpanStart(span), equalTo(3))
        assertThat(spanned.getSpanEnd(span), equalTo(5))
        assertThat(spanned.getSpans(0, 2, Object::class.java), equalTo(emptyArray()))
        assertThat(spanned.getSpans(6, 11, Object::class.java), equalTo(emptyArray()))
    }
}