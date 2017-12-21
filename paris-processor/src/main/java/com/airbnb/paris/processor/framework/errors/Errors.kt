package com.airbnb.paris.processor.framework.errors

import java.util.*
import javax.annotation.processing.*
import javax.tools.*

internal object Errors {

    private val loggedExceptions: MutableList<Exception> = ArrayList()

    fun log(e: Exception) {
        loggedExceptions.add(e)
    }

    fun printLoggedErrorsIfAny(messager: Messager) {
        if (loggedExceptions.isEmpty()) {
            return
        }

        // In case of a build failure only the first error is displayed, so this concatenates all of them in one
        // to help with debugging
        val concatenatedErrorsBuilder = StringBuilder()
        val iterator = loggedExceptions.iterator()
        while (iterator.hasNext()) {
            val exception = iterator.next()
            concatenatedErrorsBuilder.append("\n\n").append(exception.toString())
            iterator.remove()
        }
        messager.printMessage(Diagnostic.Kind.ERROR, concatenatedErrorsBuilder.toString())
    }
}
