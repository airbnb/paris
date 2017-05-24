package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.StyleableField
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceScanner
import com.airbnb.paris.processor.utils.Errors
import com.airbnb.paris.processor.utils.ProcessorException
import com.airbnb.paris.processor.utils.check
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror

internal class StyleableFieldInfo private constructor(
        val enclosingElement: Element,
        val elementType: TypeMirror,
        val elementName: String,
        val styleableResId: AndroidResourceId,
        val defaultValueResId: AndroidResourceId?) {

    companion object {

        fun fromEnvironment(roundEnv: RoundEnvironment, resourceScanner: AndroidResourceScanner): List<StyleableFieldInfo> {
            return roundEnv.getElementsAnnotatedWith(StyleableField::class.java)
                    .mapNotNull {
                        try {
                            fromElement(resourceScanner, it)
                        } catch (e: ProcessorException) {
                            Errors.log(e)
                            null
                        }
                    }
        }

        @Throws(ProcessorException::class)
        private fun fromElement(resourceScanner: AndroidResourceScanner, element: Element): StyleableFieldInfo {
            check(!element.modifiers.contains(Modifier.PRIVATE) && !element.modifiers.contains(Modifier.PROTECTED), element) {
                "Fields annotated with @StyleableField can't be private or protected"
            }

            val attr = element.getAnnotation(StyleableField::class.java)

            val enclosingElement = element.enclosingElement

            val elementType = element.asType()

            val elementName = element.simpleName.toString()

            val styleableResId = resourceScanner.getId(Attr::class.java, element, attr.value)
            var defaultValueResId: AndroidResourceId? = null
            if (attr.defaultValue != -1) {
                defaultValueResId = resourceScanner.getId(Attr::class.java, element, attr.defaultValue)
            }

            return StyleableFieldInfo(
                    enclosingElement,
                    elementType,
                    elementName,
                    styleableResId,
                    defaultValueResId)
        }
    }
}
