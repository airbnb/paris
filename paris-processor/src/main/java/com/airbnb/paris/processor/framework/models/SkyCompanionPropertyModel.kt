package com.airbnb.paris.processor.framework.models

import com.airbnb.paris.processor.framework.JavaCodeBlock
import com.airbnb.paris.processor.framework.KotlinCodeBlock
import com.airbnb.paris.processor.framework.SkyProcessor
import com.airbnb.paris.processor.framework.isJava
import com.airbnb.paris.processor.framework.siblings
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

/**
 * Applies to Java fields and Kotlin properties
 */
abstract class SkyCompanionPropertyModel(val element: VariableElement) : SkyModel {

    val enclosingElement: TypeElement = element.enclosingElement as TypeElement
    val type: TypeMirror = element.asType()
    val name: String = element.simpleName.toString()
    val getterElement: Element
    val javaGetter: JavaCodeBlock
    val kotlinGetter: KotlinCodeBlock

    init {
        if (element.isJava()) {
            getterElement = element
            javaGetter = JavaCodeBlock.of("\$N", element.simpleName)
        } else {
            // In Kotlin the annotated element is a private static field which is accompanied by a Companion method

            val getterName = "get${name.capitalize()}"
            getterElement = element.siblings().asSequence()
                .single {
                    it is TypeElement && it.simpleName.toString() == "Companion"
                }
                .enclosedElements
                .single {
                    val elementSimpleName = it.simpleName.toString()
                    it is ExecutableElement &&
                            // If the property is public the name of the getter function will be prepended with "get". If it's internal, it will also
                            // be appended with "$" and an arbitrary string for obfuscation purposes.
                            (elementSimpleName == getterName || elementSimpleName.startsWith("$getterName$"))
                }

            javaGetter = JavaCodeBlock.of("Companion.\$N()", getterElement.simpleName)
        }

        kotlinGetter = KotlinCodeBlock.of("%N()", getterElement.simpleName)
    }
}

abstract class SkyCompanionPropertyModelFactory<T : SkyCompanionPropertyModel>(
    override val processor: SkyProcessor,
    annotationClass: Class<out Annotation>
) : SkyModelFactory<T, VariableElement>(processor, annotationClass) {

    override fun filter(element: Element): Boolean = element.kind == ElementKind.FIELD
}
