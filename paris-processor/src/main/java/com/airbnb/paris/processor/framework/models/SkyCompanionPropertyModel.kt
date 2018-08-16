package com.airbnb.paris.processor.framework.models

import com.airbnb.paris.processor.framework.*
import javax.lang.model.element.*
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
                    it is ExecutableElement && it.simpleName.toString() == getterName
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
