package com.airbnb.paris.processor.framework

import androidx.room.compiler.processing.XType
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import java.lang.reflect.Type
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass


internal inline fun FileSpec.Builder.function(
    name: String,
    block: FunSpec.Builder.() -> Unit = {}
) {
    addFunction(FunSpec.builder(name).apply(block).build())
}

internal fun FunSpec.Builder.receiver(receiver: TypeMirror) {
    receiver(receiver.asTypeName())
}

internal inline fun FunSpec.Builder.addParameter(
    name: String,
    type: Type,
    block: ParameterSpec.Builder.() -> Unit = {}
) {
    addParameter(ParameterSpec.builder(name, type).apply(block).build())
}

internal inline fun FunSpec.Builder.addParameter(
    name: String,
    type: KClass<*>,
    block: ParameterSpec.Builder.() -> Unit = {}
) {
    addParameter(ParameterSpec.builder(name, type).apply(block).build())
}

internal inline fun FunSpec.Builder.addParameter(
    name: String,
    type: JavaTypeName,
    block: ParameterSpec.Builder.() -> Unit = {}
) {
    addParameter(name, type.toKPoet(), block)
}

internal inline fun FunSpec.Builder.addParameter(
    name: String,
    type: TypeName,
    block: ParameterSpec.Builder.() -> Unit = {}
) {
    addParameter(ParameterSpec.builder(name, type).apply(block).build())
}

internal fun FunSpec.Builder.parameter(
    name: String,
    type: Type,
    block: ParameterSpec.Builder.() -> Unit = {}
): ParameterSpec {
    return ParameterSpec.builder(name, type).apply(block).build().also {
        addParameter(it)
    }
}

internal fun FunSpec.Builder.parameter(
    name: String,
    type: KotlinTypeName,
    block: ParameterSpec.Builder.() -> Unit = {}
): ParameterSpec {
    return ParameterSpec.builder(name, type).apply(block).build().also {
        addParameter(it)
    }
}

internal fun FunSpec.Builder.receiver(
    type: XType,
): FunSpec.Builder {
    return receiver(type.typeNameKotlin())
}

internal fun ParameterSpec.Builder.addAnnotation(type: JavaClassName) {
    addAnnotation(type.toKPoet())
}

fun XType.typeNameKotlin(): KotlinTypeName = typeName.toKPoet()

