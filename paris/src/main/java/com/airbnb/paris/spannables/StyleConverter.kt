package com.airbnb.paris.spannables

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.*
import com.airbnb.paris.R
import com.airbnb.paris.styles.Style
import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper

/**
 * Class responsible for creating a {@link Spanned} given an input text and a list of styles.
 */
internal class StyleConverter(val context: Context) {

    data class MarkupItem(val range: IntRange, val style: Style)

    fun createSpannable(text: String, markup: Set<MarkupItem>): Spanned {

        val string = SpannableString(text)

        markup.forEach { markupItem ->
            spansFromStyle(markupItem.style).forEach {
                string.setSpan(it, markupItem.range.start, markupItem.range.endInclusive, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        return string
    }

    private fun spansFromStyle(style: Style): List<Any> {

        val attributes = style.obtainStyledAttributes(context, R.styleable.Paris_Spannable)

        val textAppearance = attributes.spanAt(R.styleable.Paris_Spannable_android_textAppearance) { TextAppearanceSpan(context, attributes.getResourceId(it)) }
        val fontFamily = attributes.spanAt(R.styleable.Paris_Spannable_android_fontFamily) { TypefaceSpan(attributes.getString(it)) }
        val typeFace = attributes.spanAt(R.styleable.Paris_Spannable_android_typeface) { TypefaceSpan(attributes.getString(it)) }
        val textStyle = attributes.spanAt(R.styleable.Paris_Spannable_android_textStyle) { StyleSpan(attributes.getInt(it)) }
        val textSize = attributes.spanAt(R.styleable.Paris_Spannable_android_textSize) { AbsoluteSizeSpan(attributes.getDimensionPixelSize(it)) }
        val foregroundColor = attributes.spanAt(R.styleable.Paris_Spannable_android_textColor) { ForegroundColorSpan(attributes.getColorStateList(it).defaultColor) }

        return listOf(
            textAppearance,
            fontFamily,
            typeFace,
            textStyle,
            textSize,
            foregroundColor
        ).filterNotNull()

    }

    private fun TypedArrayWrapper.spanAt(index: Int, converter: (Int) -> Any): Any? = if (hasValue(index)) converter(index) else null
}