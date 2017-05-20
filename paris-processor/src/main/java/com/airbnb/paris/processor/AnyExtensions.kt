package com.airbnb.paris.processor

import javax.lang.model.element.Element

@Throws(ProcessorException::class)
fun Any.check(value: Boolean, element: Element, lazyMessage: () -> String) {
    check(value) {
        "${element.simpleName}: ${lazyMessage()}"
    }
}

@Throws(ProcessorException::class)
fun Any.check(value: Boolean, lazyMessage: () -> String) {
    if (!value) throw ProcessorException(lazyMessage())
}