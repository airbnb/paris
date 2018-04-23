/**
 * Extensions to help convert JavaPoet objects to KotlinPoet.
 */
package com.airbnb.paris.processor.framework

import com.squareup.javapoet.TypeName
import com.squareup.kotlinpoet.*
import javax.lang.model.element.Modifier

internal typealias JavaClassName = com.squareup.javapoet.ClassName
internal typealias JavaTypeName = com.squareup.javapoet.TypeName
internal typealias JavaWildcardTypeName = com.squareup.javapoet.WildcardTypeName
internal typealias JavaArrayTypeName = com.squareup.javapoet.ArrayTypeName
internal typealias JavaTypeVariableName = com.squareup.javapoet.TypeVariableName
internal typealias JavaParametrizedTypeName = com.squareup.javapoet.ParameterizedTypeName
internal typealias JavaParameterSpec = com.squareup.javapoet.ParameterSpec
internal typealias JavaFieldSpec = com.squareup.javapoet.FieldSpec
internal typealias JavaAnnotationSpec = com.squareup.javapoet.AnnotationSpec
internal typealias JavaTypeSpec = com.squareup.javapoet.TypeSpec
internal typealias JavaCodeBlock = com.squareup.javapoet.CodeBlock
internal typealias KotlinClassName = com.squareup.kotlinpoet.ClassName
internal typealias KotlinParameterizedTypeName = com.squareup.kotlinpoet.ParameterizedTypeName
internal typealias KotlinTypeName = com.squareup.kotlinpoet.TypeName
internal typealias KotlinWildcardTypeName = com.squareup.kotlinpoet.WildcardTypeName
internal typealias KotlinTypeVariableName = com.squareup.kotlinpoet.TypeVariableName
internal typealias KotlinParameterSpec = com.squareup.kotlinpoet.ParameterSpec
internal typealias KotlinAnnotationSpec = com.squareup.kotlinpoet.AnnotationSpec
internal typealias KotlinTypeSpec = com.squareup.kotlinpoet.TypeSpec
internal typealias KotlinCodeBlock = com.squareup.kotlinpoet.CodeBlock

private val javaUtilPkg = "java.util"
private val javaLangPkg = "java.lang"
private val kotlinCollectionsPkg = "kotlin.collections"
private val kotlinPkg = "kotlin"

internal fun JavaClassName.toKPoet(): KotlinClassName {

    val simpleNames = getSimpleNamesInKotlin()
    val packageName = getPackageNameInKotlin()

    return KotlinClassName(
        packageName,
        simpleNames.first(),
        *simpleNames.drop(1).toTypedArray()
    )
}

/** Some classes, like List or Byte have the same class name but a different package for their kotlin equivalent. */
private fun JavaClassName.getPackageNameInKotlin(): String {
    if (packageName() in listOf(javaUtilPkg, javaLangPkg) && simpleNames().size == 1) {

        val transformedPkg = if (isBoxedPrimitive) {
            kotlinPkg
        } else {
            when (simpleName()) {
                "Collection",
                "List",
                "Map",
                "Set",
                "Iterable" -> kotlinCollectionsPkg
                "CharSequence",
                "String" -> kotlinPkg
                else -> null
            }
        }

        if (transformedPkg != null) {
            return transformedPkg
        }
    }

    return packageName()
}

/** Some classes, notably Integer and Character, have a different simple name in Kotlin. */
private fun JavaClassName.getSimpleNamesInKotlin(): List<String> {
    val originalNames = simpleNames()

    if (isBoxedPrimitive) {
        val transformedName = when (originalNames.first()) {
            "Integer" -> "Int"
            "Character" -> "Char"
            else -> null
        }

        if (transformedName != null) {
            return listOf(transformedName)
        }
    }

    return originalNames
}

// Does not support transferring annotations
internal fun JavaWildcardTypeName.toKPoet() =
    if (!lowerBounds.isEmpty()) {
        KotlinWildcardTypeName.supertypeOf(lowerBounds.first().toKPoet())
    } else {
        KotlinWildcardTypeName.subtypeOf(upperBounds.first().toKPoet())
    }

// Does not support transferring annotations
internal fun JavaParametrizedTypeName.toKPoet() = KotlinParameterizedTypeName.get(
    this.rawType.toKPoet(),
    *typeArguments.toKPoet().toTypedArray()
)

// Does not support transferring annotations
internal fun JavaArrayTypeName.toKPoet(): KotlinTypeName {

    // Kotlin has special classes for primitive arrays
    if (componentType.isPrimitive) {
        val kotlinArrayType = when (componentType) {
            TypeName.BYTE -> "ByteArray"
            TypeName.SHORT -> "ShortArray"
            TypeName.CHAR -> "CharArray"
            TypeName.INT -> "IntArray"
            TypeName.FLOAT -> "FloatArray"
            TypeName.DOUBLE -> "DoubleArray"
            TypeName.LONG -> "LongArray"
            TypeName.BOOLEAN -> "BooleanArray"
            else -> null
        }

        if (kotlinArrayType != null) {
            return KotlinClassName(kotlinPkg, kotlinArrayType)
        }
    }

    return KotlinParameterizedTypeName.get(
        KotlinClassName(kotlinPkg, "Array"),
        this.componentType.toKPoet()
    )
}

// Does not support transferring annotations
internal fun JavaTypeVariableName.toKPoet() = KotlinTypeVariableName.invoke(
    name,
    *bounds.toKPoet().toTypedArray()
)

internal fun JavaTypeName.toKPoet(): KotlinTypeName = when (this) {
    JavaTypeName.BOOLEAN -> BOOLEAN
    JavaTypeName.BYTE -> BYTE
    JavaTypeName.SHORT -> SHORT
    JavaTypeName.CHAR -> CHAR
    JavaTypeName.INT -> INT
    JavaTypeName.LONG -> LONG
    JavaTypeName.FLOAT -> FLOAT
    JavaTypeName.DOUBLE -> DOUBLE
    JavaTypeName.OBJECT -> ANY
    JavaTypeName.VOID -> UNIT
    is JavaClassName -> toKPoet()
    is JavaParametrizedTypeName -> toKPoet()
    is JavaArrayTypeName -> toKPoet()
    is JavaTypeVariableName -> toKPoet()
    is JavaWildcardTypeName -> toKPoet()
    else -> throw IllegalArgumentException("Unsupported type: ${this::class.simpleName}")
}

internal fun <T : JavaTypeName> Iterable<T>.toKPoet() = map { it.toKPoet() }

internal fun JavaParameterSpec.toKPoet(): KotlinParameterSpec {

    // A param name in java might be reserved in kotlin
    val paramName = if (name in KOTLIN_KEYWORDS) name + "Param" else name

    return KotlinParameterSpec.builder(
        paramName,
        type.toKPoet(),
        *modifiers.toKModifier().toTypedArray()
    ).build()

}

internal fun Iterable<JavaParameterSpec>.toKParams() = map { it.toKPoet() }

internal fun Iterable<Modifier>.toKModifier(): List<KModifier> =
    map { it.toKModifier() }.filter { it != null }.map { it!! }

internal fun Modifier.toKModifier() = when (this) {
    Modifier.PUBLIC -> KModifier.PUBLIC
    Modifier.PRIVATE -> KModifier.PRIVATE
    Modifier.PROTECTED -> KModifier.PROTECTED
    Modifier.FINAL -> KModifier.FINAL
    Modifier.ABSTRACT -> KModifier.ABSTRACT
    else -> null
}

// https://github.com/JetBrains/kotlin/blob/master/core/descriptors/src/org/jetbrains/kotlin/renderer/KeywordStringsGenerated.java
private val KOTLIN_KEYWORDS = setOf(
    "package",
    "as",
    "typealias",
    "class",
    "this",
    "super",
    "val",
    "var",
    "fun",
    "for",
    "null",
    "true",
    "false",
    "is",
    "in",
    "throw",
    "return",
    "break",
    "continue",
    "object",
    "if",
    "try",
    "else",
    "while",
    "do",
    "when",
    "interface",
    "typeof"
)

