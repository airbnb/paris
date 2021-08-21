package com.airbnb.paris.processor.framework

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XProcessingEnv
import com.airbnb.paris.processor.utils.enclosingElementIfApplicable
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * Most annotation processor classes will need access to [Filer], [Messager], [Elements] and [Types], among other things.
 */
interface WithJavaSkyProcessor : WithSkyProcessor {

    val processor: JavaSkyProcessor
    override val processingEnv: XProcessingEnv
        get() = processor.processingEnv

    val filer get() = processor.filer
    val messager get() = processor.messager
    val elements get() = processor.elements
    val types get() = processor.types
    val memoizer get() = processor.memoizer

    override val loggedMessages: MutableList<Message>
        get() = processor.loggedMessages

    fun erasure(type: TypeMirror): TypeMirror = types.erasure(type)

    fun isSameType(type1: TypeMirror, type2: TypeMirror) = types.isSameType(type1, type2)

    fun isSubtype(type1: TypeMirror, type2: TypeMirror) = types.isSubtype(type1, type2)

    // ClassName

    fun JavaClassName.toTypeElement(): TypeElement = elements.getTypeElement(reflectionName())

    fun JavaClassName.toTypeMirror(): TypeMirror = toTypeElement().asType()


    // TypeMirror

    fun TypeMirror.asTypeElement(): TypeElement = types.asElement(this) as TypeElement


    // Android specific

    fun isView(type: TypeMirror): Boolean = isSubtype(type, processor.memoizer.androidViewClassType)

    override fun printLogsIfAny() {
        loggedMessages.forEach { message ->
            val kind = when (message.severity) {
                Message.Severity.Warning -> Diagnostic.Kind.WARNING
                Message.Severity.Error -> Diagnostic.Kind.ERROR
            }
            val element = message.element
            processingEnv.messager.printMessage(kind, message.message + " [$element : ${element?.enclosingElementIfApplicable}]", element)
        }
    }
}

interface WithSkyProcessor {

    val processingEnv: XProcessingEnv

    val loggedMessages: MutableList<Message>

    fun logError(element: XElement? = null, lazyMessage: () -> String) {
        log(Message.Severity.Error, element, lazyMessage)
    }

    fun logWarning(element: XElement? = null, lazyMessage: () -> String) {
        log(Message.Severity.Warning, element, lazyMessage)
    }

    fun log(severity: Message.Severity, element: XElement? = null, lazyMessage: () -> String) {
        loggedMessages.add(Message(severity, lazyMessage(), element))
    }

    fun printLogsIfAny()
}
