package com.airbnb.paris.processor.utils

import androidx.room.compiler.processing.KnownTypeNames
import androidx.room.compiler.processing.XAnnotated
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XType
import com.squareup.javapoet.TypeName
import kotlin.contracts.contract

fun XElement.isFieldElement(): Boolean {
    contract {
        returns(true) implies (this@isFieldElement is XFieldElement)
    }
    return this is XFieldElement
}

fun XAnnotated.hasAnyAnnotationBySimpleName(annotationSimpleNames: Iterable<String>): Boolean {
    return when (this) {
        is KspAnnotated -> {
            annotations().any { annotation ->
                annotationSimpleNames.any { targetName ->
                    annotation.shortName.asString() == targetName
                }
            }
        }
        is JavacElement -> {
            element.annotationMirrors.any { annotation ->
                annotationSimpleNames.any { targetName ->
                    annotation.annotationType.asElement().simpleName.toString() == targetName
                }
            }
        }
        else -> error("unsupported $this")
    }
}

private object BoxedTypeNames {
    val BOXED_FLOAT = TypeName.FLOAT.box()
    val BOXED_BOOLEAN = TypeName.BOOLEAN.box()
}

fun XType.isFloat(): Boolean = typeName == TypeName.FLOAT || typeName == BoxedTypeNames.BOXED_FLOAT

fun XType.isBoolean(): Boolean = typeName == TypeName.BOOLEAN || typeName == BoxedTypeNames.BOXED_BOOLEAN

