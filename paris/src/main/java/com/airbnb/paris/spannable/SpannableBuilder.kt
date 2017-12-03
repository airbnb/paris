package com.airbnb.paris.spannable

import android.content.Context
import android.support.annotation.StyleRes
import android.widget.TextView
import com.airbnb.paris.Style
import com.airbnb.paris.styles.ResourceStyle

/**
 * Class that allows you to create a CharSequence with markup objects inferred from styles.
 */
class SpannableBuilder internal constructor(context: Context) {

    private val spans = ArrayList<StyleConverter.MarkupItem>()
    private val stringBuilder = StringBuilder()
    private val converter = StyleConverter(context)

    @JvmOverloads
    fun append(text: String, @StyleRes styleRes: Int = 0): SpannableBuilder {
        append(text, ResourceStyle(styleRes))
        return this
    }

    fun append(text: String, style: Style): SpannableBuilder {
        val currentStrLength = stringBuilder.length
        spans.add(StyleConverter.MarkupItem(IntRange(currentStrLength, currentStrLength + text.length), style))
        stringBuilder.append(text)
        return this
    }

    fun build(): CharSequence = converter.createSpannable(stringBuilder.toString(), spans)

    fun applyTo(textView : TextView) {
        textView.text = build()
    }
}