package com.airbnb.paris.spannable

import android.graphics.Color
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.text.style.AbsoluteSizeSpan
import com.airbnb.paris.styles.ProgrammaticStyle
import com.airbnb.paris.test.R
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StyleConverterTest {

    private val converter = StyleConverter(InstrumentationRegistry.getTargetContext()!!)

    @Test
    fun textSize(){

        val bigTextStyle = ProgrammaticStyle.builder()
                .put(android.R.attr.textSize, 30)
                .build()

        val spanned = converter.createSpannable("Hello world", listOf(StyleConverter.MarkupItem(IntRange(0, 5), bigTextStyle)))

        val spans = spanned.getSpans(0, 5, AbsoluteSizeSpan::class.java)
        assertThat(spans.size, equalTo(1))
        assertThat(spans[0].size, equalTo(30))

        assertThat(spanned.getSpans(6, 11, Object::class.java).size, equalTo(0))
    }

    @Test
    fun textColor(){

    }
}