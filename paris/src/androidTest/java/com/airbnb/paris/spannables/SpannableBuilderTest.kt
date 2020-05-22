package com.airbnb.paris.spannables

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.airbnb.paris.styles.ProgrammaticStyle
import com.airbnb.paris.styles.ResourceStyle
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpannableBuilderTest {

    val context = InstrumentationRegistry.getTargetContext()!!

    @Test
    fun emptyString() {

        val builder = SpannableBuilder()

        assertThat(builder.markupItems.isEmpty(), equalTo(true))
        assertThat(builder.stringBuilder.toString(), equalTo(""))
    }

    @Test
    fun appendingStyledStrings() {

        val dummyStyle = ProgrammaticStyle.builder().build();

        val builder = SpannableBuilder()
            .append("Good", 1)
            .append(" Morning", dummyStyle)

        assertThat(
            builder.markupItems, equalTo(
                setOf(
                    StyleConverter.MarkupItem(IntRange(0, 4), ResourceStyle(1)),
                    StyleConverter.MarkupItem(IntRange(4, 12), dummyStyle)
                )
            )
        )
        assertThat(builder.stringBuilder.toString(), equalTo("Good Morning"))
    }

    @Test
    fun appendingNonStyledStrings() {

        val builder = SpannableBuilder()
            .append("Good")
            .append(" Morning")

        assertThat(builder.markupItems, equalTo(emptySet()))
        assertThat(builder.stringBuilder.toString(), equalTo("Good Morning"))
    }
}