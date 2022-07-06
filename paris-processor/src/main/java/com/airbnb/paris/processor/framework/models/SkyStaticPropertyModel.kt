package com.airbnb.paris.processor.framework.models

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.compat.XConverters.toJavac
import androidx.room.compiler.processing.compat.XConverters.toXProcessing
import androidx.room.compiler.processing.isMethod
import com.airbnb.paris.processor.BaseProcessor
import com.airbnb.paris.processor.framework.JavaCodeBlock
import com.airbnb.paris.processor.framework.siblings
import com.airbnb.paris.processor.utils.enclosingElementIfCompanion
import com.airbnb.paris.processor.utils.isFieldElement
import com.airbnb.paris.processor.utils.isJavac
import com.airbnb.paris.processor.utils.javaGetterSyntax
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

/**
 * Applies to Java static fields and Kotlin companion properties.
 * Element will be a method element in javac as a getter function, and a field property in KSP.
 */
abstract class SkyStaticPropertyModel(val element: XElement, env: XProcessingEnv) : SkyModel {

    private val directEnclosingElement: XTypeElement = when (element) {
        is XMethodElement -> element.enclosingElement as XTypeElement
        is XFieldElement -> element.enclosingElement as XTypeElement
        else -> error("Unsupported type $element of type ${element.javaClass}")
    }

    val enclosingElement: XTypeElement get() = directEnclosingElement.enclosingElementIfCompanion

    // Code for use in java source to access the property via a getter function.
    val javaGetter: JavaCodeBlock
    val getterElement: XElement

    init {
        when (element) {
            is XMethodElement -> {
                val (_, getter) = findGetterPropertyFromSyntheticFunction(element)
                    ?: error(
                        "${element}: Could not find getter for property annotated with @StyleableChild. " +
                                "This probably means the property is private or protected."
                    )
                getterElement = getter

                // Method case for kotlin companion property in javac/kapt
                // Original source is kotlin, so java interop will use a getter function
                javaGetter = JavaCodeBlock.of("Companion.\$N()", getter.jvmName)
            }
            is XFieldElement -> {

                if (element.isJavac) {
                    val javacElement = element.toJavac()
                    if (directEnclosingElement.isCompanionObject() || directEnclosingElement.hasAnnotation(Metadata::class)) {
                        // Kotlin source viewed in javac/kapt.
                        // Java representation is a field when the annotation target is "Field"
                        val companionFunctions = javacElement.siblings()
                            .single {
                                it is TypeElement && it.simpleName.toString() == "Companion"
                            }
                            .enclosedElements
                            .filterIsInstance<ExecutableElement>()
                            .ifEmpty {
                                error("$element ${element.enclosingElement} - could not get companion object")
                            }

                        // If the property is public the name of the getter function will be prepended with "get". If it's internal, it will also
                        // be appended with "$" and an arbitrary string for obfuscation purposes.
                        // Kotlin 1.4.x contains BOTH at once, but only the none synthetic one can be used, so we check for the real one first.
                        val getterName = "get${element.name.capitalize()}"
                        val companionGetter = companionFunctions.firstOrNull {
                            val elementSimpleName = it.simpleName.toString()
                            elementSimpleName == getterName
                        } ?: companionFunctions.firstOrNull {
                            val elementSimpleName = it.simpleName.toString()
                            elementSimpleName.startsWith("$getterName$")
                        } ?: error("$element ${element.enclosingElement} - could not find companion property $getterName")

                        getterElement = companionGetter.toXProcessing(env)
                        javaGetter = JavaCodeBlock.of("Companion.\$N()", companionGetter.simpleName)
                    } else {
                        // java source accessing java static property uses field reference directly.
                        getterElement = element
                        javaGetter = JavaCodeBlock.of("\$N", element.name)
                    }
                } else {
                    // KSP case
                    val enclosingType = element.enclosingElement as? XTypeElement
                    if (enclosingType?.isCompanionObject() == true) {
                        // kotlin source case. ksp represents elements as a private field in the companion object.
                        // we need to expose its public getter.
                        val getterSyntax = element.javaGetterSyntax(env)

                        // We don't have a getter in ksp - would have to create a synthetic one.
                        getterElement = element
                        javaGetter = JavaCodeBlock.of("Companion.\$N", getterSyntax)
                    } else {
                        // Java source case, can access java field directly
                        javaGetter = JavaCodeBlock.of("\$N", element.name)
                        getterElement = element
                    }
                }
            }
            else -> error("Unsupported type $element of type ${element.javaClass}")
        }
    }
}

abstract class SkyStaticPropertyModelFactory<T : SkyStaticPropertyModel>(
    processor: BaseProcessor,
    annotationClass: Class<out Annotation>
) : JavaSkyModelFactory<T, XElement>(processor, annotationClass) {

    override fun filter(element: XElement): Boolean {
        // Will be a field in the kotlin/ksp case or the java source case
        return element.isFieldElement() && element.isStatic()
                // Kapt/javac sees kotlin companion properties as static getter function
                || (element.isMethod() && element.isStatic() && (element.enclosingElement as? XTypeElement)?.isCompanionObject() == true)
    }
}
