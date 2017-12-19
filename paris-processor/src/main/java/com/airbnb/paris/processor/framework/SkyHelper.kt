package com.airbnb.paris.processor.framework

import com.airbnb.paris.processor.utils.*
import com.squareup.javapoet.*
import javax.lang.model.element.*
import javax.lang.model.type.*

abstract class SkyHelper<out T : SkyProcessor>(val processor: T) {

    val filer = processor.filer
    val messager = processor.messager
    val elements = processor.elements
    val types = processor.types

    protected fun isSameType(type1: TypeMirror, type2: TypeMirror) = types.isSameType(type1, type2)

    protected fun isSubtype(type1: TypeMirror, type2: TypeMirror) = types.isSubtype(type1, type2)

    protected fun erasure(type: TypeMirror): TypeMirror = types.erasure(type)

    protected fun ClassName.toTypeMirror(): TypeMirror = toTypeElement(elements).asType()

    protected fun TypeMirror.asTypeElement(): TypeElement = this.asTypeElement(types)

    protected fun Element.getPackageElement(): PackageElement = elements.getPackageOf(this)

    protected fun codeBlock(block: CodeBlock.Builder.() -> Unit): CodeBlock {
        val builder = CodeBlock.builder()
        builder.block()
        return builder.build()
    }

    protected fun annotation(type: Class<*>, block: AnnotationSpec.Builder.() -> Unit): AnnotationSpec {
        val builder = AnnotationSpec.builder(type)
        builder.block()
        return builder.build()
    }

    protected fun classType(name: String, block: TypeSpec.Builder.() -> Unit): TypeSpec {
        val builder = TypeSpec.classBuilder(name)
        builder.block()
        return builder.build()
    }

    protected fun classType(name: ClassName, block: TypeSpec.Builder.() -> Unit): TypeSpec {
        val builder = TypeSpec.classBuilder(name)
        builder.block()
        return builder.build()
    }

    protected fun constructor(block: MethodSpec.Builder.() -> Unit): MethodSpec {
        val builder = MethodSpec.constructorBuilder()
        builder.block()
        return builder.build()
    }

    protected fun method(name: String, block: MethodSpec.Builder.() -> Unit): MethodSpec {
        val builder = MethodSpec.methodBuilder(name)
        builder.block()
        return builder.build()
    }

    protected fun parameter(type: TypeName, name: String, block: (ParameterSpec.Builder.() -> Unit)? = null): ParameterSpec {
        val builder = ParameterSpec.builder(type, name)
        block?.invoke(builder)
        return builder.build()
    }

    protected fun writeJavaFile(packageName: String, typeSpec: TypeSpec) {
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer)
    }
}
