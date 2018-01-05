package com.airbnb.paris.spannables

import android.graphics.Color
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import com.airbnb.paris.attribute_values.ColorValue
import com.airbnb.paris.styles.ProgrammaticStyle
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StyleConverterTest {

    private val converter = StyleConverter(InstrumentationRegistry.getTargetContext()!!)
    private val helloWorld = "Hello World"

    @Test
    fun textSize(){

        val bigTextStyle = ProgrammaticStyle.builder()
                .put(android.R.attr.textSize, 30)
                .build()

        val spanned = converter.createSpannable(helloWorld, setOf(StyleConverter.MarkupItem(IntRange(3, 5), bigTextStyle)))

        val spans = spanned.getSpans(0, helloWorld.length, AbsoluteSizeSpan::class.java)
        assertThat(spans.size, equalTo(1))

        val span = spans[0]
        assertThat(span.size, equalTo(30))
        assertThat(spanned.getSpanStart(span), equalTo(3))
        assertThat(spanned.getSpanEnd(span), equalTo(5))
        assertThat(spanned.getSpans(0, 2, Object::class.java), equalTo(emptyArray()))
        assertThat(spanned.getSpans(6, 11, Object::class.java), equalTo(emptyArray()))
    }

    @Test
    fun textColor(){

        val cyanTextStyle = ProgrammaticStyle.builder()
                .put(android.R.attr.textColor, ColorValue(Color.CYAN))
                .build()

        val spanned = converter.createSpannable(helloWorld, setOf(StyleConverter.MarkupItem(IntRange(3, 5), cyanTextStyle)))

        val spans = spanned.getSpans(0, helloWorld.length, ForegroundColorSpan::class.java)
        assertThat(spans.size, equalTo(1))

        val span = spans[0]
        assertThat(span.foregroundColor, equalTo(Color.CYAN))
        assertThat(spanned.getSpanStart(span), equalTo(3))
        assertThat(spanned.getSpanEnd(span), equalTo(5))
        assertThat(spanned.getSpans(0, 2, Object::class.java), equalTo(emptyArray()))
        assertThat(spanned.getSpans(6, 11, Object::class.java), equalTo(emptyArray()))
    }
}