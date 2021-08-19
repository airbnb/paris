package com.airbnb.paris.processor.framework.models

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.paris.processor.framework.JavaSkyProcessor
import com.airbnb.paris.processor.framework.siblings
import javax.lang.model.element.ExecutableElement

/**
 * Applies to Java fields and Kotlin properties
 */
abstract class SkyPropertyModel(val processingEnv: XProcessingEnv, val element: XElement) : SkyModel {

    val enclosingElement: XTypeElement = when (element) {
        is XMethodElement -> element.enclosingElement as XTypeElement
        is XFieldElement -> element.enclosingElement as XTypeElement
        else -> error("Unsupported type $element")
    }

    val type: XType
    val name: String

    /**
     * The getter could be a field or a method depending on if the annotated class is in Java or in Kotlin
     */
    val getterElement: XElement

    /**
     * What you'd call to get the property value
     */
    val getter: String

    init {
        when (element) {
            is XMethodElement -> {
                val env = processingEnv as JavacProcessingEnv
                val (propertyName, getterFunction) = findGetterPropertyFromSyntheticFunction(env, element)

                name = propertyName
                getterElement = getterFunction
                getter = "${getterFunction.name}()"
                type = getterFunction.returnType
            }
            is XFieldElement -> {
                name = element.name
                getterElement = element
                getter = name
                type = element.type
            }
            else -> error("Unsupported type $element")
        }

        if (type.toString() == "void") {
            error("$element has void type")
        }
    }
}

// In Kotlin it's a synthetic empty static method whose name is <property>$annotations that ends
// up being annotated.
// In kotlin 1.4.0+ the method is changed to start with "get", so we need to handle both cases
private fun findGetterPropertyFromSyntheticFunction(env:JavacProcessingEnv, element: XMethodElement): GetterResult {
    val name = element.name
        .substringBefore("\$annotations")
        // get prefix will only exist for kotlin 1.4
        .removePrefix("get")
        .decapitalize()

    val syntheticMethod = (element as JavacMethodElement).element

    val getterName = "get${name.capitalize()}"
    val getters = syntheticMethod.siblings().asSequence()
        .filterIsInstance<ExecutableElement>()
        .filter { it.parameters.isEmpty() }

    // If the property is public the name of the getter function will be prepended with "get". If it's internal, it will also
    // be appended with "$" and an arbitrary string for obfuscation purposes.
    // In kotlin 1.4.0 both versions will be present, so we check for the real getter first.
    val kotlinGetterElement = getters.firstOrNull {
        val elementSimpleName = it.simpleName.toString()
        elementSimpleName == getterName
    } ?: getters.firstOrNull {
        val elementSimpleName = it.simpleName.toString()
        elementSimpleName.startsWith("$getterName$")
    } ?: error(
        "${element}: Could not find getter ($getterName) for property annotated with @StyleableChild. " +
                "This probably means the property is private or protected."
    )
    return GetterResult(name, env.wrapExecutableElement(kotlinGetterElement) as XMethodElement)

    // For example, in Kotlin 1.4.30 this property is turned into java code like:
    //    @StyleableChild(R2.styleable.Test_WithStyleableChildKotlinView_test_arbitraryStyle)
    //    val arbitrarySubView = View(context)
    // ->
    //   @NotNull
    //   private final View arbitrarySubView;
    //
    //   /** @deprecated */
    //   // $FF: synthetic method
    //   @StyleableChild(1725)
    //   public static void getArbitrarySubView$annotations() {
    //   }
    //
    //   @NotNull
    //   public final View getArbitrarySubView() {
    //      return this.arbitrarySubView;
    //   }
}

private data class GetterResult(val propertyName: String, val getterFunction: XMethodElement)

typealias SkyFieldModel = SkyPropertyModel

abstract class SkyFieldModelFactory<T : SkyPropertyModel>(
    processor: JavaSkyProcessor,
    annotationClass: Class<out Annotation>
) : JavaSkyModelFactory<T, XElement>(processor, annotationClass)
