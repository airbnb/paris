package com.airbnb.paris.spannable

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.airbnb.paris.styles.ResourceStyle
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpannableBuilderTest {

    val context = InstrumentationRegistry.getTargetContext()!!

    @Test
    fun testEmptyString() {

        val builder = SpannableBuilder()

        assertThat(builder.markupItems.isEmpty(), equalTo(true))
        assertThat(builder.stringBuilder.toString(), equalTo(""))
    }

    @Test
    fun testAppendingStyledString() {

        val builder = SpannableBuilder()
                .append("Good", 1)
                .append(" Morning", 2)

        assertThat(builder.markupItems, equalTo(setOf(
                StyleConverter.MarkupItem(IntRange(0, 4), ResourceStyle(1)),
                StyleConverter.MarkupItem(IntRange(4, 12), ResourceStyle(2))
        )))
        assertThat(builder.stringBuilder.toString(), equalTo("Good Morning"))
    }
}