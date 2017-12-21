package com.airbnb.paris.processor.framework

import com.squareup.javapoet.*
import javax.lang.model.element.*

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

