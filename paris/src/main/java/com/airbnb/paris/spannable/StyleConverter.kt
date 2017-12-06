package com.airbnb.paris.spannable

import android.content.Context
import android.support.annotation.StyleRes
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import com.airbnb.paris.Style
import com.airbnb.paris.TypedArrayWrapper
import com.airbnb.paris.styles.ProgrammaticStyle
import com.airbnb.paris.styles.ResourceStyle

/**
 * Class responsible for creating a {@link Spanned} given an input text and a list of styles.
 */
internal class StyleConverter(val context: Context) {

    private val attrs = intArrayOf(android.R.attr.textSize, android.R.attr.textColor)

    data class MarkupItem(val range: IntRange, val style: Style)

    fun createSpannable(text: String, markup: List<MarkupItem>): Spanned {

        val builder = SpannableStringBuilder(text)

        for (markupItem in markup) {
            val style = markupItem.style
            for (span in spansFromStyle(style)) {
                builder.setSpan(span, markupItem.range.start, markupItem.range.endInclusive, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }

        return builder
    }

    private fun spansFromStyle(style: Style): List<Any> {
        val attributes = style.obtainStyledAttributes(context, attrs)

        val textSize = attributes.spanAt(0) { AbsoluteSizeSpan(attributes.getDimensionPixelSize(it)) }
        val foregroundColor = attributes.spanAt(1) { ForegroundColorSpan(attributes.getColorStateList(it).defaultColor) }

        return listOf(textSize, foregroundColor).filterNotNull()
    }

    private fun TypedArrayWrapper.spanAt(index: Int, converter: (Int) -> Any): Any? = if (hasValue(index)) converter(index) else null
}