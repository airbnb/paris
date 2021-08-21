package com.airbnb.paris.processor.utils

import androidx.room.compiler.processing.XAnnotated
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XExecutableElement
import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XMemberContainer
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.compat.XConverters.toJavac
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
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
