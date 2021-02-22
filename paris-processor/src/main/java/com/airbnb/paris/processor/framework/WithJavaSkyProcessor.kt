package com.airbnb.paris.processor.framework

import com.airbnb.paris.processor.abstractions.XElement
import com.airbnb.paris.processor.abstractions.XProcessingEnv
import com.airbnb.paris.processor.abstractions.javac.JavacElement
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
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

    // Element

    fun Element.getPackageElement(): PackageElement = elements.getPackageOf(this)

    // TypeMirror

    fun TypeMirror.asTypeElement(): TypeElement = types.asElement(this) as TypeElement

    /**
     * Kapt replaces unknown types by "NonExistentClass". This can happen when code refers to generated classes. For example:
     *
     *
     * ```
     * @Style val myStyle = myViewStyle { }
     * ```
     *
     * myViewStyle is a generated function so the type of the field will be "NonExistentClass" when processed with kapt.
     *
     * This behavior can be altered by using `kapt { correctErrorTypes = true }` in the Gradle config.
     */
    fun TypeMirror.isNonExistent() = this.toString() == "error.NonExistentClass"

    // Android specific

    fun isView(type: TypeMirror): Boolean = isSubtype(type, processor.memoizer.androidViewClassType)

    override fun printLogsIfAny() {
        loggedMessages.forEach {
            val kind = when (it.severity) {
                Message.Severity.Warning -> Diagnostic.Kind.WARNING
                Message.Severity.Error -> Diagnostic.Kind.ERROR
            }
            if (it.element != null) {
                val javaElement = (it.element as JavacElement).element
                val message = it.message + " (${javaElement.toStringId()})\n "
                messager.printMessage(kind, message, javaElement)
            } else {
            messager.printMessage(kind, it.message)
            }
        }
    }
}



//interface WithKspSkyProcessor : WithSkyProcessor {
//    var options: Map<String, String>
//    var kotlinVersion: KotlinVersion
//    var codeGenerator: CodeGenerator
//    var logger: KSPLogger
//
//    override fun printLogsIfAny() {
//        loggedMessages.forEach {
//            val symbol = (it.element as KspElement).declaration
//            when (it.severity) {
//                Message.Severity.Warning -> logger.warn(it.message, symbol)
//                Message.Severity.Error -> logger.error(it.message, symbol)
//            }
//        }
//    }
//}

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
