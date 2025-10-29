package com.airbnb.paris.processor.framework

import androidx.room.compiler.processing.XElement


class Message(
    val severity: Severity,
    val message: String,
    element: XElement?,
    elementDetailsExtractor: ((XElement) -> String)? = null
) {
    val elementDetails: String? = element?.let { el ->
        elementDetailsExtractor?.invoke(el).orEmpty()
    }

    enum class Severity {
        Note, Warning, Error
    }
}



