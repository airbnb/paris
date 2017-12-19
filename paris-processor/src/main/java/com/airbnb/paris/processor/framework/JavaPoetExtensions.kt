package com.airbnb.paris.processor.framework

import com.airbnb.paris.processor.*
import com.squareup.javapoet.*
import javax.lang.model.element.*
import javax.lang.model.type.*

fun AnnotationSpec.Builder.value(block: CodeBlock.Builder.() -> Unit) {
    val codeBuilder = CodeBlock.builder()
    codeBuilder.block()
    addMember("value", codeBuilder.build())
}

fun AnnotationSpec.Builder.value(format: String, vararg args: Any) {
    addMember("value", format, *args)
}

fun MethodSpec.Builder.override() {
    addAnnotation(Override::class.java)
}

fun MethodSpec.Builder.final() {
    addModifiers(Modifier.FINAL)
}

fun MethodSpec.Builder.protected() {
    addModifiers(Modifier.PROTECTED)
}

fun MethodSpec.Builder.public() {
    addModifiers(Modifier.PUBLIC)
}

fun MethodSpec.Builder.static() {
    addModifiers(Modifier.STATIC)
}

fun MethodSpec.Builder.controlFlow(controlFlow: String, arg: Any, block: MethodSpec.Builder.() -> Unit) {
    controlFlow(controlFlow, arrayOf(arg), block)
}

fun MethodSpec.Builder.controlFlow(controlFlow: String, args: Array<Any> = emptyArray(), block: MethodSpec.Builder.() -> Unit) {
    beginControlFlow(controlFlow, *args)
    block()
    endControlFlow()
}

fun TypeSpec.Builder.codeBlock(block: CodeBlock.Builder.() -> Unit): CodeBlock {
    val builder = CodeBlock.builder()
    builder.block()
    return builder.build()
}

fun TypeSpec.Builder.annotation(type: Class<*>, block: AnnotationSpec.Builder.() -> Unit) {
    val annotationBuilder = AnnotationSpec.builder(type)
    annotationBuilder.block()
    addAnnotation(annotationBuilder.build())
}

fun TypeSpec.Builder.constructor(block: MethodSpec.Builder.() -> Unit) {
    val methodBuilder = MethodSpec.constructorBuilder()
    methodBuilder.block()
    addMethod(methodBuilder.build())
}

fun TypeSpec.Builder.method(name: String, block: MethodSpec.Builder.() -> Unit) {
    val methodBuilder = MethodSpec.methodBuilder(name)
    methodBuilder.block()
    addMethod(methodBuilder.build())
}

fun TypeSpec.Builder.abstract() {
    addModifiers(Modifier.ABSTRACT)
}

fun TypeSpec.Builder.final() {
    addModifiers(Modifier.FINAL)
}

fun TypeSpec.Builder.protected() {
    addModifiers(Modifier.PROTECTED)
}

fun TypeSpec.Builder.public() {
    addModifiers(Modifier.PUBLIC)
}

fun TypeSpec.Builder.static() {
    addModifiers(Modifier.STATIC)
}

// TODO Should be SkyProcessor
internal val TypeSpec.Builder.filer get() = ParisProcessor.INSTANCE.filer
internal val TypeSpec.Builder.messager get() = ParisProcessor.INSTANCE.messager
internal val TypeSpec.Builder.elements get() = ParisProcessor.INSTANCE.elements
internal val TypeSpec.Builder.types get() = ParisProcessor.INSTANCE.types

internal val TypeSpec.Builder.RFinder get() = ParisProcessor.INSTANCE.RFinder

internal fun TypeSpec.Builder.isSameType(type1: TypeMirror, type2: TypeMirror) =
        types.isSameType(type1, type2)

//protected fun isSubtype(type1: TypeMirror, type2: TypeMirror) = types.isSubtype(type1, type2)
//
//protected fun erasure(type: TypeMirror): TypeMirror = types.erasure(type)
//
//protected fun ClassName.toTypeMirror(): TypeMirror = toTypeElement(elements).asType()
//
internal fun TypeMirror.asTypeElement(): TypeElement =
        ParisProcessor.INSTANCE.types.asElement(this) as TypeElement
//
//protected fun Element.getPackageElement(): PackageElement = elements.getPackageOf(this)


fun CodeBlock.Builder.annotation(type: Class<*>, block: AnnotationSpec.Builder.() -> Unit): AnnotationSpec {
    val builder = AnnotationSpec.builder(type)
    builder.block()
    return builder.build()
}

fun classType(name: String, block: TypeSpec.Builder.() -> Unit): TypeSpec {
    val builder = TypeSpec.classBuilder(name)
    builder.block()
    return builder.build()
}

fun classType(name: ClassName, block: TypeSpec.Builder.() -> Unit): TypeSpec {
    val builder = TypeSpec.classBuilder(name)
    builder.block()
    return builder.build()
}

fun TypeSpec.method(name: String, block: MethodSpec.Builder.() -> Unit): MethodSpec {
    val builder = MethodSpec.methodBuilder(name)
    builder.block()
    return builder.build()
}

fun MethodSpec.parameter(type: TypeName, name: String, block: (ParameterSpec.Builder.() -> Unit)? = null): ParameterSpec {
    val builder = ParameterSpec.builder(type, name)
    block?.invoke(builder)
    return builder.build()
}
