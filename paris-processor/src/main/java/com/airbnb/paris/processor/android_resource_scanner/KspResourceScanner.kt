package com.airbnb.paris.processor.android_resource_scanner

import androidx.room.compiler.processing.XElement
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.impl.kotlin.KSAnnotationImpl
import com.squareup.javapoet.ClassName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
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
        val resourceReference = matchingArg.reference ?: return null

        return AndroidResourceId(
            value,
            ClassName.bestGuess(matchingArg.reference.substringBeforeLast(".")),
            resourceName = matchingArg.reference.substringAfterLast(".")
        )
    }

    private fun processAnnotationWithResource(annotation: KSAnnotation): List<AnnotationWithReferenceValue> {
        val annotationEntry = (annotation as KSAnnotationImpl).ktAnnotationEntry
        return annotationEntry.valueArguments.zip(annotation.arguments).map { (valueArgument, ksValueArgument) ->
            val argumentName = ksValueArgument.name?.asString()
            val argumentValue = ksValueArgument.value

            val fullyQualifiedReference: String? = valueArgument.getArgumentExpression()?.let { ex ->
                val annotationReference: String = fqNameFromExpression(ex)?.asString() ?: return@let null

                val annotationReferencePrefix = annotationReference.substringBefore(".")
                val packageName: String =
                    annotationEntry.containingKtFile.importDirectives.firstOrNull { import ->
                        val importPath = import.importPath?.toString() ?: return@firstOrNull false
                        importPath.endsWith(".$annotationReferencePrefix") ||
                                (!importPath.contains(".") && importPath == annotationReferencePrefix)
                    }?.importPath?.toString() ?: run {
                        if (annotationReferencePrefix.firstOrNull()?.isUpperCase() == true) {
                            annotationEntry.containingKtFile.packageFqName.asString()
                        } else {
                            ""
                        }
                    }

                packageName + (if (packageName.isNotEmpty()) "." else "") + annotationReference
            }

            AnnotationWithReferenceValue(
                argumentName,
                argumentValue,
                fullyQualifiedReference
            )
        }
    }

    data class AnnotationWithReferenceValue(
        val name: String?,
        val value: Any?,
        val reference: String?
    )

    // From https://github.com/JetBrains/kotlin/blob/92d200e093c693b3c06e53a39e0b0973b84c7ec5/compiler/psi/src/org/jetbrains/kotlin/psi/KtImportDirective.java
    private fun fqNameFromExpression(expression: KtExpression): FqName? {
        return when (expression) {
            is KtDotQualifiedExpression -> {
                val parentFqn: FqName? = fqNameFromExpression(expression.receiverExpression)
                val child: Name = nameFromExpression(expression.selectorExpression) ?: return parentFqn
                parentFqn?.child(child)
            }
            is KtSimpleNameExpression -> {
                FqName.topLevel(expression.getReferencedNameAsName())
            }
            else -> {
                error("Can't construct fqn for: " + expression.javaClass.toString())
            }
        }
    }

    private fun nameFromExpression(expression: KtExpression?): Name? {
        if (expression == null) {
            return null
        }
        return if (expression is KtSimpleNameExpression) {
            expression.getReferencedNameAsName()
        } else {
            error("Can't construct name for: " + expression.javaClass.toString())
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