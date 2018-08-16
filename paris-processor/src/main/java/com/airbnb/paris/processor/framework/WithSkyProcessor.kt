package com.airbnb.paris.processor.framework

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
interface WithSkyProcessor {

    val processor: SkyProcessor

    val filer get() = processor.filer
    val messager get() = processor.messager
    val elements get() = processor.elements
    val types get() = processor.types
    val kaptOutputPath get() = processor.kaptOutputPath
    val loggedMessages get() = processor.loggedMessages

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

    fun isView(type: TypeMirror): Boolean = isSubtype(type, AndroidClassNames.VIEW.toTypeMirror())

    // Error handling

    fun logError(element: Element, lazyMessage: () -> String) {
        logError { "${element.toStringId()}: ${lazyMessage()}" }
    }

    fun logError(lazyMessage: () -> String) {
        loggedMessages.add(Message(Diagnostic.Kind.ERROR, lazyMessage()))
    }

    fun logWarning(element: Element, lazyMessage: () -> String) {
        logError { "${element.toStringId()}: ${lazyMessage()}" }
    }

    fun logWarning(lazyMessage: () -> String) {
        loggedMessages.add(Message(Diagnostic.Kind.WARNING, lazyMessage()))
    }

    fun printLogsIfAny(messager: Messager) {
        loggedMessages.forEach {
            messager.printMessage(it.kind, it.message)
        }
    }
}
