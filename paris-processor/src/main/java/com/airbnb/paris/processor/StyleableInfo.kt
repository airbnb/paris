package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Style
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceScanner
import com.airbnb.paris.processor.utils.Errors
import com.airbnb.paris.processor.utils.ProcessorException
import com.airbnb.paris.processor.utils.check
import com.airbnb.paris.processor.utils.packageName
import com.sun.tools.javac.code.Attribute
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

/**
 * [attrs] will be empty if [styleableResourceName] is and vice versa
 */
internal class StyleableInfo private constructor(
        val attrs: List<AttrInfo>,
        val styleableAttrs: List<AttrInfo>,
        val elementPackageName: String,
        val elementName: String,
        val elementType: TypeMirror,
        val styleableResourceName: String,
        val dependencies: List<TypeMirror>,
        val styles: List<StyleInfo>) {

    companion object {

        fun fromEnvironment(roundEnv: RoundEnvironment, resourceScanner: AndroidResourceScanner, classesToAttrsInfo: Map<Element, List<AttrInfo>>): List<StyleableInfo> {
            return roundEnv.getElementsAnnotatedWith(Styleable::class.java)
                    .mapNotNull {
                        try {
                            fromElement(resourceScanner, it as TypeElement, classesToAttrsInfo[it] ?: emptyList())
                        } catch(e: ProcessorException) {
                            Errors.log(e)
                            null
                        }
                    }
        }

        @Throws(ProcessorException::class)
        private fun fromElement(resourceScanner: AndroidResourceScanner, element: TypeElement, attrs: List<AttrInfo>): StyleableInfo {

            val styleableAttrs = attrs.filter { it.isElementStyleable }

            val elementPackageName = element.packageName
            val elementName = element.simpleName.toString()
            val elementType = element.asType()

            val styleable = element.getAnnotation(Styleable::class.java)
            val styleableResourceName = styleable.value

            val styleableName: String = Styleable::class.java.name
            // We use annotation mirrors here because the dependency classes might not exist yet
            val dependencies = element.annotationMirrors
                    // Find @Styleable
                    .find { styleableName == it.annotationType.toString() }!!
                    // Find the "dependencies" parameter
                    .elementValues.filterKeys { "dependencies" == it.simpleName.toString() }
                    .flatMap {
                        @Suppress("UNCHECKED_CAST")
                        (it.value.value as List<Attribute.Class>).map { it.value }
                    }

            val styles = styleable.styles.map {
                StyleInfo(it.name, resourceScanner.getId(Style::class.java, element, it.id))
            }

            check(!styleableResourceName.isEmpty() || !dependencies.isEmpty() || !styles.isEmpty(), element) {
                "@Styleable declaration must have at least a value, or a dependency, or a style"
            }
            check(styleableResourceName.isEmpty() || !attrs.isEmpty()) {
                "Do not specify the @Styleable value parameter if no class members are annotated with @Attr"
            }

            return StyleableInfo(
                    attrs,
                    styleableAttrs,
                    elementPackageName,
                    elementName,
                    elementType,
                    styleableResourceName,
                    dependencies,
                    styles)
        }
    }
}
