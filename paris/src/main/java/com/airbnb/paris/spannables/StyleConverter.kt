package com.airbnb.paris.spannables

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.text.style.TypefaceSpan
import com.airbnb.paris.R
import com.airbnb.paris.styles.Style

/**
 * Class responsible for creating a {@link Spanned} given an input text and a list of styles.
 */
internal class StyleConverter(val context: Context) {

    data class MarkupItem(val range: IntRange, val style: Style)

    fun createSpannable(text: String, markup: Set<MarkupItem>): Spanned {

        val string = SpannableString(text)

        // working around lint issue incorrectly flagging forEach as being a java language feature only for api 24+
        markup.forEachIndexed { _, markupItem ->
            spansFromStyle(markupItem.style).forEachIndexed { _, it ->
                string.setSpan(it, markupItem.range.start, markupItem.range.endInclusive, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        return string
    }

    private fun spansFromStyle(style: Style): List<Any> {

        val attributes = style.obtainStyledAttributes(context, R.styleable.Paris_Spannable)

        return mapOf<Int, (Int) -> Any>(
            R.styleable.Paris_Spannable_android_textAppearance to { index -> TextAppearanceSpan(context, attributes.getResourceId(index)) },
            R.styleable.Paris_Spannable_android_fontFamily to { index -> TypefaceSpan(attributes.getString(index)) },
            R.styleable.Paris_Spannable_android_typeface to { index -> TypefaceSpan(attributes.getString(index)) },
            R.styleable.Paris_Spannable_android_textStyle to { index -> StyleSpan(attributes.getInt(index)) },
            R.styleable.Paris_Spannable_android_textSize to { index -> AbsoluteSizeSpan(attributes.getDimensionPixelSize(index)) },
            R.styleable.Paris_Spannable_android_textColor to { index -> ForegroundColorSpan(attributes.getColorStateList(index)!!.defaultColor) }
        ).filter { (index, _) -> attributes.hasValue(index) }
            .map { (index, converter) -> converter(index) }
    }
}