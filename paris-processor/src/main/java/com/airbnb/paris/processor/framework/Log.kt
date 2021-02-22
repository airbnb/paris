package com.airbnb.paris.processor.framework

import com.airbnb.paris.processor.abstractions.XElement

class Message(val severity: Severity, val message: String, val element: XElement?) {

    enum class Severity {
        Warning, Error
    }
}



