package com.airbnb.paris.processor.android_resource_scanner

import androidx.room.compiler.processing.XElement
import com.airbnb.paris.processor.utils.containingPackage
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.impl.java.KSAnnotationJavaImpl
import com.google.devtools.ksp.symbol.impl.kotlin.KSAnnotationImpl
import com.squareup.javapoet.ClassName
import org.jetbrains.kotlin.com.intellij.psi.PsiAnnotation
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaFile
import org.jetbrains.kotlin.com.intellij.psi.PsiNameValuePair
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.ValueArgument
import kotlin.reflect.KClass

class KspResourceScanner : ResourceScanner {
    private val cache = mutableMapOf<Pair<KClass<out Annotation>, XElement>, List<AnnotationWithReferenceValue>>()

    override fun getId(annotation: KClass<out Annotation>, element: XElement, value: Int): AndroidResourceId? {
        val annotationArgs = cache.getOrPut(annotation to element) {
            val annotationBox = element.getAnnotation(annotation) ?: return@getOrPut emptyList()
            val ksAnnotation = annotationBox.getFieldWithReflection<KSAnnotation>("annotation")
            processAnnotationWithResource(ksAnnotation)
        }

        val matchingArg = annotationArgs.firstOrNull { it.value == value } ?: return null

        return matchingArg.toAndroidResourceId()
    }

    private fun processAnnotationWithResource(annotation: KSAnnotation): List<AnnotationWithReferenceValue> {
        val packageName = annotation.containingPackage.orEmpty()
        return when (annotation) {
            is KSAnnotationImpl -> processKtAnnotation(annotation.ktAnnotationEntry, annotation, packageName)
            is KSAnnotationJavaImpl -> processJavaAnnotation(annotation.psi, annotation, packageName)
            else -> emptyList()
        }
    }

    private fun processJavaAnnotation(
        psi: PsiAnnotation,
        annotation: KSAnnotationJavaImpl,
        packageName: String
    ): List<AnnotationWithReferenceValue> {
        return psi.parameterList
            .attributes
            .zip(annotation.arguments)
            .map { (psiNameValue, ksValueArgument) ->
                AnnotationWithReferenceValue(
                    name = ksValueArgument.name?.asString(),
                    value = ksValueArgument.value,
                    reference = extractJavaReferenceAnnotationArgument(psiNameValue, annotation, packageName)
                )
            }
    }

    private fun extractJavaReferenceAnnotationArgument(
        psiNameValue: PsiNameValuePair,
        annotation: KSAnnotationJavaImpl,
        packageName: String
    ): String? {
        // eg: R.layout.foo, com.example.R.layout.foo, layout.foo, etc
        return psiNameValue.value?.text?.let { annotationReference ->
            extractReferenceAnnotationArgument(annotationReference) { annotationReferencePrefix ->
                findMatchingImportPackageJava(annotation.psi, annotationReferencePrefix, packageName)
            }
        }
    }

    private fun extractReferenceAnnotationArgument(
        // eg: R.layout.foo, com.example.R.layout.foo, layout.foo, etc
        annotationReference: String,
        packagePrefixLookup: (annotationReferencePrefix: String) -> String,
    ): String? {
        // First part of dot reference, eg: "R"
        val annotationReferencePrefix = annotationReference.substringBefore(".", "").ifEmpty { return null }

        val packagePrefix: String = packagePrefixLookup(annotationReferencePrefix)

        return packagePrefix + (if (packagePrefix.isNotEmpty()) "." else "") + annotationReference
    }

    private fun processKtAnnotation(
        annotationEntry: KtAnnotationEntry,
        annotation: KSAnnotation,
        packageName: String
    ): List<AnnotationWithReferenceValue> {
        return annotationEntry.valueArguments
            .zip(annotation.arguments)
            .map { (valueArgument, ksValueArgument) ->

                AnnotationWithReferenceValue(
                    name = ksValueArgument.name?.asString(),
                    value = ksValueArgument.value,
                    reference = extractKotlinReferenceAnnotationArgument(valueArgument, annotationEntry, packageName)
                )
            }
    }

    private fun extractKotlinReferenceAnnotationArgument(
        valueArgument: ValueArgument,
        annotationEntry: KtAnnotationEntry,
        packageName: String
    ): String? {
        return valueArgument.getArgumentExpression()?.let { ex ->

            // eg: R.layout.foo, com.example.R.layout.foo, layout.foo, etc
            val annotationReference: String = fqNameFromExpression(ex)?.asString() ?: return@let null

            extractReferenceAnnotationArgument(annotationReference) { annotationReferencePrefix ->
                findMatchingImportPackageKt(annotationEntry, annotationReferencePrefix, packageName)
            }
        }
    }

    private fun findMatchingImportPackageJava(
        annotationEntry: PsiAnnotation,
        annotationReferencePrefix: String,
        packageName: String
    ): String {
        val importedNames = (annotationEntry.containingFile as? PsiJavaFile)
            ?.importList
            ?.importStatements
            ?.mapNotNull { it.qualifiedName }
            ?: emptyList()

        return findMatchingImportPackage(importedNames, annotationReferencePrefix, packageName)
    }

    private fun findMatchingImportPackageKt(
        annotationEntry: KtAnnotationEntry,
        annotationReferencePrefix: String,
        packageName: String
    ): String {
        val importedNames = annotationEntry
            .containingKtFile
            .importDirectives
            .mapNotNull { it.importPath?.toString() }

        return findMatchingImportPackage(importedNames, annotationReferencePrefix, packageName)
    }

    private fun findMatchingImportPackage(
        importedNames: List<String>,
        annotationReferencePrefix: String,
        packageName: String
    ): String {
        return importedNames.firstNotNullOfOrNull { importedName ->

            when {
                importedName.endsWith(".$annotationReferencePrefix") -> {
                    // import com.example.R
                    // R.layout.my_layout -> R
                    importedName.substringBeforeLast(".$annotationReferencePrefix")
                }
                (!importedName.contains(".") && importedName == annotationReferencePrefix) -> {
                    // import foo
                    // foo.R.layout.my_layout -> foo
                    ""
                }
                // TODO: 8/25/21 star imports are handled? (java and kotlin)
                else -> null
            }
        } ?: run {
            // If first character in the reference is upper case, and we didn't find a matching import,
            // assume that it is a class reference in the same package (ie R class is in the same package, so we use the same package name)
            if (annotationReferencePrefix.firstOrNull()?.isUpperCase() == true) {
                packageName
            } else {
                // Reference is already fully qualified so we don't need to prepend package info to the reference
                ""
            }
        }
    }

    data class AnnotationWithReferenceValue(
        val name: String?,
        val value: Any?,
        val reference: String?
    ) {
        fun toAndroidResourceId(): AndroidResourceId? {
            if (value !is Int || reference == null) return null

            val resourceInfo = when {
                ".R2." in reference || reference.startsWith("R2.") -> {
                    extractResourceInfo(reference, "R2")
                }
                ".R." in reference || reference.startsWith("R.") -> {
                    extractResourceInfo(reference, "R")
                }
                else -> {
                    error("Unsupported resource reference $reference")
                }
            }

            return AndroidResourceId(
                value,
                // Regardless of if the input is R or R2, we always need the generated code to reference R
                ClassName.get(resourceInfo.packageName, "R", resourceInfo.rSubclassName),
                resourceName = resourceInfo.resourceName
            )
        }

        /**
         * @param reference fully qualified resource reference. eg com.example.R.layout.my_view
         * @param rClassSimpleName ie R or R2
         */
        private fun extractResourceInfo(reference: String, rClassSimpleName: String): ResourceReferenceInfo {
            // get package before R and resource details after R
            val packageAndResourceType = reference.split(".$rClassSimpleName.").also {
                check(it.size == 2) { "Unexpected annotation value reference pattern $reference" }
            }

            val packageName = packageAndResourceType[0]

            val (rSubclass, resourceName) = packageAndResourceType[1].split(".").also {
                check(it.size == 2) { "Unexpected annotation value reference pattern $reference" }
            }

            return ResourceReferenceInfo(
                packageName = packageName,
                rSimpleName = rClassSimpleName,
                rSubclassName = rSubclass,
                resourceName = resourceName
            )
        }
    }

    private data class ResourceReferenceInfo(
        val packageName: String,
        val rSimpleName: String,
        val rSubclassName: String,
        val resourceName: String
    )

    // From https://github.com/JetBrains/kotlin/blob/92d200e093c693b3c06e53a39e0b0973b84c7ec5/compiler/psi/src/org/jetbrains/kotlin/psi/KtImportDirective.java
    private fun fqNameFromExpression(expression: KtExpression): FqName? {
        return when (expression) {
            is KtDotQualifiedExpression -> {
                val parentFqn: FqName? = fqNameFromExpression(expression.receiverExpression)
                val child: Name = expression.selectorExpression?.let { nameFromExpression(it) } ?: return parentFqn
                parentFqn?.child(child)
            }
            is KtSimpleNameExpression -> {
                FqName.topLevel(expression.getReferencedNameAsName())
            }
            else -> {
                null
            }
        }
    }

    private fun nameFromExpression(expression: KtExpression): Name? {
        return if (expression is KtSimpleNameExpression) {
            expression.getReferencedNameAsName()
        } else {
            null
        }
    }
}

/**
 * Easy way to retrieve the value of a field via reflection.
 *
 * @param fieldName Name of the field on this class
 * @param U The type of the field..
 */
inline fun <reified U> Any.getFieldWithReflection(fieldName: String): U {
    return this.javaClass.getDeclaredField(fieldName).let {
        it.isAccessible = true
        val value = it.get(this)
        check(value is U) {
            "Expected field '$fieldName' to be ${U::class.java.simpleName} but got a ${value.javaClass.simpleName}"
        }
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        value
    }
}