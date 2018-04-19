package com.airbnb.paris.processor.framework

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.type.TypeMirror


fun FileSpec.Builder.function(name: String, block: FunSpec.Builder.() -> Unit) {
    val methodBuilder = FunSpec.builder(name)
    methodBuilder.block()
    addFunction(methodBuilder.build())
}

fun FunSpec.Builder.receiver(reciever: TypeMirror) {
    receiver(reciever.asTypeName())
}