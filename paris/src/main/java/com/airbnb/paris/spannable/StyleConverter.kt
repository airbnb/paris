package com.airbnb.paris.spannable

import android.content.Context
import android.support.annotation.StyleRes
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import com.airbnb.paris.R
import com.airbnb.paris.Style
import com.airbnb.paris.TypedArrayWrapper
import com.airbnb.paris.styles.ProgrammaticStyle
import com.airbnb.paris.styles.ResourceStyle

/**
 * Class responsible for creating a {@link Spanned} given an input text and a list of styles.
 */
internal class StyleConverter(val context: Context) {

    data class MarkupItem(val range: IntRange, val style: Style)

    fun createSpannable(text: String, markup: Set<MarkupItem>): Spanned {

        val builder = SpannableString(text)

        markup.forEach { markupItem ->
            spansFromStyle(markupItem.style).forEach {
                builder.setSpan(it, markupItem.range.start, markupItem.range.endInclusive, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        return builder
    }

    private fun spansFromStyle(style: Style): List<Any> {
        val attributes = style.obtainStyledAttributes(context, R.styleable.Paris_TextView)

        val textSize = attributes.spanAt(R.styleable.Paris_TextView_android_textSize) { AbsoluteSizeSpan(attributes.getDimensionPixelSize(it)) }
        val foregroundColor = attributes.spanAt(R.styleable.Paris_TextView_android_textColor) { ForegroundColorSpan(attributes.getColorStateList(it).defaultColor) }

        return listOf(textSize, foregroundColor).filterNotNull()
    }

    private fun TypedArrayWrapper.spanAt(index: Int, converter: (Int) -> Any): Any? = if (hasValue(index)) converter(index) else null
}