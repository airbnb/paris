package com.airbnb.paris.spannable

import android.content.Context
import android.support.annotation.StyleRes
import android.widget.TextView
import com.airbnb.paris.styles.ResourceStyle
import com.airbnb.paris.styles.Style

/**
 * Class that allows you to create a CharSequence with markup objects inferred from styles.
 */
class SpannableBuilder internal constructor() {

    internal var markupItems: Set<StyleConverter.MarkupItem> = HashSet()
    internal val stringBuilder = StringBuilder()

    @JvmOverloads
    fun append(text: String, @StyleRes styleRes: Int = 0): SpannableBuilder {
        if (styleRes == 0) {
            stringBuilder.append(text)
        } else {
            append(text, ResourceStyle(styleRes))
        }
        return this
    }

    fun append(text: String, style: Style): SpannableBuilder {
        val currentStrLength = stringBuilder.length
        markupItems += StyleConverter.MarkupItem(IntRange(currentStrLength, currentStrLength + text.length), style)
        stringBuilder.append(text)
        return this
    }

    fun build(context: Context): CharSequence = StyleConverter(context).createSpannable(stringBuilder.toString(), markupItems)

    fun applyTo(textView : TextView) {
        textView.text = build(textView.context)
    }
}