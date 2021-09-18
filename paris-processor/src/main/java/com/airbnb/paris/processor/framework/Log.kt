package com.airbnb.paris.processor.framework

import androidx.room.compiler.processing.XElement


class Message(val severity: Severity, val message: String, val element: XElement?) {

    enum class Severity {
        Note, Warning, Error
    }
}



