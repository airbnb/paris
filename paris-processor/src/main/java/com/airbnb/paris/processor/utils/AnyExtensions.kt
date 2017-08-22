package com.airbnb.paris.processor.utils

import javax.lang.model.element.Element

@Throws(ProcessorException::class)
fun Any.check(value: Boolean, element: Element, lazyMessage: () -> String) {
    if (!value) fail(element, lazyMessage)
}

@Throws(ProcessorException::class)
fun Any.check(value: Boolean, lazyMessage: () -> String) {
    if (!value) fail(lazyMessage)
}

@Throws(ProcessorException::class)
fun Any.fail(element: Element, lazyMessage: () -> String) {
    fail { "${element.simpleName}: ${lazyMessage()}" }
}

@Throws(ProcessorException::class)
fun Any.fail(lazyMessage: () -> String) {
    throw ProcessorException(lazyMessage())
}
