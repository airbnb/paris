package com.airbnb.paris.processor.framework.errors

import javax.lang.model.element.Element

@Throws(ProcessorException::class)
internal fun check(value: Boolean, element: Element, lazyMessage: () -> String) {
    if (!value) error(element, lazyMessage)
}

@Throws(ProcessorException::class)
internal fun error(element: Element, lazyMessage: () -> String): Nothing {
    error { "${element.simpleName}: ${lazyMessage()}" }
}
