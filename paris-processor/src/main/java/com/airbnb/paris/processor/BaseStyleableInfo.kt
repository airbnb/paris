package com.airbnb.paris.processor

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.utils.*
import javax.lang.model.element.*
import javax.lang.model.type.*
import javax.lang.model.util.*

open internal class BaseStyleableInfo constructor(
        val elementPackageName: String,
        val elementName: String,
        /**
         * If the styleable class is not a proxy, will be equal to [viewElementType]. Otherwise,
         * will refer to the proxy class
         */
        val elementType: TypeMirror,
        /**
         * If the styleable class is not a proxy, will be equal to [elementType]. Refers to the view
         * class
         */
        val viewElementType: TypeMirror,
        val styleableResourceName: String
) {
    companion object {

        @Throws(ProcessorException::class)
        fun fromElement(
                elementUtils: Elements,
                typeUtils: Types,
                element: TypeElement
        ): BaseStyleableInfo {

            val elementPackageName = element.packageName
            val elementName = element.simpleName.toString()
            val elementType = element.asType()

            val viewElementType: TypeMirror
            if (typeUtils.isSubtype(elementType, typeUtils.erasure(elementUtils.PROXY_TYPE.asType()))) {
                // Get the parameterized type, which should be the view type
                viewElementType = (element.superclass as DeclaredType).typeArguments[1]
            } else {
                viewElementType = elementType
            }

            val styleable = element.getAnnotation(Styleable::class.java)
            val styleableResourceName = styleable.value

            return BaseStyleableInfo(
                    elementPackageName,
                    elementName,
                    elementType,
                    viewElementType,
                    styleableResourceName
            )
        }
    }
}
