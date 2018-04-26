package com.airbnb.paris.processor.framework

import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.tools.Diagnostic
import javax.tools.Diagnostic.Kind.ERROR
import javax.tools.Diagnostic.Kind.WARNING

private val loggedMessages = mutableListOf<Message>()

private class Message(val kind: Diagnostic.Kind, val message: CharSequence)

internal fun logError(element: Element, lazyMessage: () -> String) {
    logError({ "${element.simpleName}: ${lazyMessage()}" })
}

internal fun logError(lazyMessage: () -> String) {
    loggedMessages.add(Message(ERROR, lazyMessage()))
}

internal fun logWarning(element: Element, lazyMessage: () -> String) {
    logError({ "${element.simpleName}: ${lazyMessage()}" })
}

internal fun logWarning(lazyMessage: () -> String) {
    loggedMessages.add(Message(WARNING, lazyMessage()))
}

internal fun printLogsIfAny(messager: Messager) {
    loggedMessages.forEach {
        messager.printMessage(it.kind, it.message)
    }
    loggedMessages.clear()
}
