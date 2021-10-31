package com.airbnb.paris.processor.utils

import androidx.room.compiler.processing.XAnnotated
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XExecutableElement
import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XMemberContainer
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.addOriginatingElement
import androidx.room.compiler.processing.compat.XConverters.toJavac
import com.airbnb.paris.processor.android_resource_scanner.getFieldWithReflection
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyAccessor
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Origin
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.squareup.kotlinpoet.OriginatingElementsHolder
import javax.lang.model.element.Element
import kotlin.contracts.contract

fun XElement.isFieldElement(): Boolean {
    contract {
        returns(true) implies (this@isFieldElement is XFieldElement)
    }
    return this is XFieldElement
}

fun XElement.isExecutableElement(): Boolean {
    contract {
        returns(true) implies (this@isExecutableElement is XExecutableElement)
    }
    return this is XExecutableElement
}

fun XAnnotated.hasAnyAnnotationBySimpleName(annotationSimpleNames: Iterable<String>): Boolean {
    return getAllAnnotations().any { annotation -> annotationSimpleNames.any { it == annotation.name } }
}

private object BoxedTypeNames {
    val BOXED_FLOAT = TypeName.FLOAT.box()
    val BOXED_BOOLEAN = TypeName.BOOLEAN.box()
}

fun XType.isFloat(): Boolean = typeName == TypeName.FLOAT || typeName == BoxedTypeNames.BOXED_FLOAT

fun XType.isBoolean(): Boolean = typeName == TypeName.BOOLEAN || typeName == BoxedTypeNames.BOXED_BOOLEAN

fun XType.isSameTypeName(other: TypeName, useRawType: Boolean = false): Boolean {
    return if (useRawType) {
        typeName.rawTypeName() == other.rawTypeName()
    } else {
        typeName == other
    }
}

internal fun TypeName.rawTypeName(): TypeName {
    return if (this is ParameterizedTypeName) {
        this.rawType
    } else {
        this
    }
}

/**
 * A bug in XProcessing throws an NPE if the package is not found. This is a workaround until the library is fixed.
 * Fix merged in https://github.com/androidx/androidx/pull/222 but waiting on the next release.
 */
fun XProcessingEnv.getTypeElementsFromPackageSafe(packageName: String): List<XTypeElement> {
    return try {
        getTypeElementsFromPackage(packageName)
    } catch (e: NullPointerException) {
        emptyList()
    }
}

val XElement.enclosingElementIfApplicable: XMemberContainer?
    get() {
        return when (this) {
            is XExecutableElement -> enclosingElement
            is XFieldElement -> enclosingElement
            else -> null
        }
    }

// The isError doesn't seem to appropriately detect a java type mirror of error.NonExistentClass for some reason.
val XType.isErrorFixed: Boolean get() = isError() || toString() == "error.NonExistentClass"

val XElement.isJavac: Boolean
    get() = try {
        this.toJavac()
        true
    } catch (e: Throwable) {
        false
    }

val XProcessingEnv.resolver: Resolver
    get() = getFieldWithReflection("_resolver")

val KSAnnotation.containingPackage: String?
    get() = parent?.containingPackage

val KSNode.containingPackage: String?
    get() {
        return when (this) {
            is KSFile -> packageName.asString()
            is KSDeclaration -> packageName.asString()
            else -> parent?.containingPackage
        }
    }

fun XFieldElement.javaGetterSyntax(env: XProcessingEnv): String {
    val ksDeclaration = getFieldWithReflection<KSPropertyDeclaration>("declaration")

    return when (ksDeclaration.origin) {
        // java to java interop references the field directly.
        Origin.JAVA, Origin.JAVA_LIB -> name
        Origin.KOTLIN, Origin.KOTLIN_LIB -> {
            val accessor = ksDeclaration.getter ?: error("No getter found for $this $enclosingElement")
            // Getter is a function call from java to kotlin
            return env.resolver.getJvmName(accessor)?.plus("()") ?: error("Getter name not found for $this $enclosingElement")
        }
        Origin.SYNTHETIC -> error("Don't know how to get jvm name for element of synthetic origin $this $enclosingElement")
    }
}

val XTypeElement.enclosingElementIfCompanion: XTypeElement
    get() = if (isCompanionObject()) enclosingTypeElement!! else this
