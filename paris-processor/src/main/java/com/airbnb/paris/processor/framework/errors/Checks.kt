package com.airbnb.paris.processor.framework.errors

import javax.lang.model.element.*

@Throws(ProcessorException::class)
internal fun Any.check(value: Boolean, element: Element, lazyMessage: () -> String) {
    if (!value) fail(element, lazyMessage)
}

@Throws(ProcessorException::class)
internal fun Any.check(value: Boolean, lazyMessage: () -> String) {
    if (!value) fail(lazyMessage)
}

@Throws(ProcessorException::class)
internal fun Any.fail(element: Element, lazyMessage: () -> String): Nothing {
    fail { "${element.simpleName}: ${lazyMessage()}" }
}

@Throws(ProcessorException::class)
internal fun Any.fail(lazyMessage: () -> String): Nothing {
    throw ProcessorException(lazyMessage())
}
