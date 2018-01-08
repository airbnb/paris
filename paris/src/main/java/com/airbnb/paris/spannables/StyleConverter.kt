package com.airbnb.paris.spannables

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import com.airbnb.paris.R
import com.airbnb.paris.styles.Style
import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper

/**
 * Class responsible for creating a {@link Spanned} given an input text and a list of styles.
 */
internal class StyleConverter(val context: Context) {

    data class MarkupItem(val range: IntRange, val style: Style)

    fun createSpannable(text: String, markup: Set<MarkupItem>): Spanned {

        val string = SpannableStringBuilder(text)

        markup.forEach { markupItem ->
            val style = markupItem.style
            spansFromStyle(style).forEach {
                string.setSpan(it, markupItem.range.start, markupItem.range.endInclusive, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        return string
    }

    private fun spansFromStyle(style: Style): List<Any> {
        val attributes = style.obtainStyledAttributes(context, R.styleable.Paris_Spannable)

        val textSize = attributes.spanAt(R.styleable.Paris_Spannable_android_textSize) { AbsoluteSizeSpan(attributes.getDimensionPixelSize(it)) }
        val foregroundColor = attributes.spanAt(R.styleable.Paris_Spannable_android_textColor) { ForegroundColorSpan(attributes.getColorStateList(it).defaultColor) }
        val fontFamily = attributes.spanAt(R.styleable.Paris_Spannable_android_fontFamily) { TypefaceSpan(attributes.getString(it)) }
        val textStyle = attributes.spanAt(R.styleable.Paris_Spannable_android_textStyle) { StyleSpan(attributes.getInt(it)) }
        val typeFace = attributes.spanAt(R.styleable.Paris_Spannable_android_typeface) { TypefaceSpan(attributes.getString(it)) }

        return listOf(textSize, foregroundColor, fontFamily, textStyle, typeFace).filterNotNull()
    }

    private fun TypedArrayWrapper.spanAt(index: Int, converter: (Int) -> Any): Any? = if (hasValue(index)) converter(index) else null
}