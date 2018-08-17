package com.airbnb.paris.sample

import android.graphics.Color
import com.airbnb.paris.extensions.textColor
import com.airbnb.paris.extensions.textSizeDp
import com.airbnb.paris.extensions.textViewStyle

// Styles can be declared completely programmatically and shared.

private val TEXT = textViewStyle {
    textColor(Color.BLACK)
}

val TEXT_HEADLINE = textViewStyle {
    add(TEXT)
    textSizeDp(26)
}

val TEXT_BODY = textViewStyle {
    add(TEXT)
    textSizeDp(16)
}
