package com.airbnb.paris.processor.framework

import javax.annotation.processing.Messager
import javax.lang.model.element.*
import javax.tools.Diagnostic
import javax.tools.Diagnostic.Kind.ERROR
import javax.tools.Diagnostic.Kind.WARNING

internal class Message(val kind: Diagnostic.Kind, val message: CharSequence)

internal fun logError(element: Element, lazyMessage: () -> String) {
    logError({ "${element.toStringId()}: ${lazyMessage()}" })
}

internal fun logError(lazyMessage: () -> String) {
    loggedMessages.add(Message(ERROR, lazyMessage()))
}

internal fun logWarning(element: Element, lazyMessage: () -> String) {
    logError({ "${element.toStringId()}: ${lazyMessage()}" })
}

internal fun logWarning(lazyMessage: () -> String) {
    loggedMessages.add(Message(WARNING, lazyMessage()))
}

internal fun Element.toStringId(): String {
    return when (this) {
        is PackageElement -> qualifiedName.toString()
        is TypeElement -> qualifiedName.toString()
        is ExecutableElement,
        is VariableElement -> "${enclosingElement.toStringId()}.$simpleName"
        else -> simpleName.toString()
    }
}

internal fun printLogsIfAny(messager: Messager) {
    loggedMessages.forEach {
        messager.printMessage(it.kind, it.message)
    }
}
