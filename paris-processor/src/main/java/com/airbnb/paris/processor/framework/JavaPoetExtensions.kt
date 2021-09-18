package com.airbnb.paris.processor.framework

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

internal fun AnnotationSpec.Builder.value(block: CodeBlock.Builder.() -> Unit) {
    val codeBuilder = CodeBlock.builder()
    codeBuilder.block()
    addMember("value", codeBuilder.build())
}

internal fun AnnotationSpec.Builder.value(format: String, vararg args: Any) {
    addMember("value", format, *args)
}

internal fun MethodSpec.Builder.override() {
    addAnnotation(Override::class.java)
}

internal fun MethodSpec.Builder.final() {
    addModifiers(Modifier.FINAL)
}

internal fun MethodSpec.Builder.protected() {
    addModifiers(Modifier.PROTECTED)
}

internal fun MethodSpec.Builder.public() {
    addModifiers(Modifier.PUBLIC)
}

internal fun MethodSpec.Builder.static() {
    addModifiers(Modifier.STATIC)
}

internal fun MethodSpec.Builder.controlFlow(controlFlow: String, arg: Any, block: MethodSpec.Builder.() -> Unit) {
    controlFlow(controlFlow, arrayOf(arg), block)
}

// TODO This interface is confusing because unless `args` is explicitly `Array<Any>` then it's the other controlFlow function that gets called, which usually results in an error.
internal fun MethodSpec.Builder.controlFlow(controlFlow: String, args: Array<Any> = emptyArray(), block: MethodSpec.Builder.() -> Unit) {
    beginControlFlow(controlFlow, *args)
    block()
    endControlFlow()
}

internal fun TypeSpec.Builder.codeBlock(block: CodeBlock.Builder.() -> Unit): CodeBlock {
    val builder = CodeBlock.builder()
    builder.block()
    return builder.build()
}

internal fun TypeSpec.Builder.annotation(type: Class<*>, block: AnnotationSpec.Builder.() -> Unit) {
    val annotationBuilder = AnnotationSpec.builder(type)
    annotationBuilder.block()
    addAnnotation(annotationBuilder.build())
}

internal fun TypeSpec.Builder.constructor(block: MethodSpec.Builder.() -> Unit) {
    val methodBuilder = MethodSpec.constructorBuilder()
    methodBuilder.block()
    addMethod(methodBuilder.build())
}

internal fun TypeSpec.Builder.method(name: String, block: MethodSpec.Builder.() -> Unit) {
    val methodBuilder = MethodSpec.methodBuilder(name)
    methodBuilder.block()
    addMethod(methodBuilder.build())
}

internal fun TypeSpec.Builder.abstract() {
    addModifiers(Modifier.ABSTRACT)
}

internal fun TypeSpec.Builder.final() {
    addModifiers(Modifier.FINAL)
}

internal fun TypeSpec.Builder.protected() {
    addModifiers(Modifier.PROTECTED)
}

internal fun TypeSpec.Builder.public() {
    addModifiers(Modifier.PUBLIC)
}

internal fun TypeSpec.Builder.static() {
    addModifiers(Modifier.STATIC)
}
