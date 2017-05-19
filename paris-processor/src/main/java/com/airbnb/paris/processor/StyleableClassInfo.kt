package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Styleable
import com.squareup.javapoet.ClassName
import com.sun.tools.javac.code.Attribute
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements

internal class StyleableClassInfo private constructor(
        val attrs: List<AttrInfo>,
        val styleableAttrs: List<AttrInfo>,
        val packageName: String,
        val name: String,
        val type: TypeMirror,
        val resourceName: String,
        val dependencies: List<TypeMirror>) {

    companion object {

        fun fromElement(elementUtils: Elements, element: Element, attrs: List<AttrInfo>): StyleableClassInfo {
            val styleableAttrs = attrs.filter { it.isView }

            val packageName = ClassName.get(element as TypeElement).packageName()
            val name = element.getSimpleName().toString()
            val type = element.asType()
            val styleable = element.getAnnotation(Styleable::class.java)
            val resourceName = styleable.value

            var dependencies: List<TypeMirror> = emptyList()
            val styleableName = Styleable::class.java.name
            // We use annotation mirrors here because the dependency classes might not exist yet
            element.annotationMirrors
                    .filter { styleableName == it.annotationType.toString() }
                    .forEach {
                        for ((key, value) in it.elementValues) {
                            if ("dependencies" == key.simpleName.toString()) {
                                @Suppress("UNCHECKED_CAST")
                                dependencies = (value.value as List<Attribute.Class?>).map {
                                    val dependencyClassQualifiedName = it.toString().removeSuffix(".class")
                                    elementUtils.getTypeElement(dependencyClassQualifiedName).asType()
                                }
                            }
                        }
                    }

            // TODO  Throw exception if no resourceName AND no dependencies

            return StyleableClassInfo(attrs, styleableAttrs, packageName, name, type, resourceName, dependencies)
        }
    }
}
