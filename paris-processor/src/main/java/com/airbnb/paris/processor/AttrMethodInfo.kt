package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Attr
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror

internal class AttrMethodInfo private constructor(val name: String, val type: TypeMirror, val id: Id) {

    companion object {

        fun fromElement(resourceProcessor: ResourceProcessor, element: Element): AttrMethodInfo {
            val name = element.getSimpleName().toString()
            val type = element.asType()
            val attr = element.getAnnotation(Attr::class.java)
            val styleableResourceValue = attr.value
            val id = resourceProcessor.getId(Attr::class.java, element, styleableResourceValue)

            return AttrMethodInfo(name, type, id)
        }
    }
}