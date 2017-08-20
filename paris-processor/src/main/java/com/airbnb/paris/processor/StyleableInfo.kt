package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Style
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceScanner
import com.airbnb.paris.processor.utils.*
import com.squareup.javapoet.ClassName
import com.sun.tools.javac.code.Attribute
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * If [styleableResourceName] isn't empty then at least one of [styleableFields] or [attrs] won't be
 * empty either
 */
internal class StyleableInfo private constructor(
        val styleableFields: List<StyleableFieldInfo>,
        val beforeStyles: List<BeforeStyleInfo>,
        val afterStyles: List<AfterStyleInfo>,
        val attrs: List<AttrInfo>,
        val elementPackageName: String,
        val elementName: String,
        val elementType: TypeMirror,
        val styleableResourceName: String,
        val dependencies: List<TypeMirror>,
        val styles: List<StyleInfo>) {

    fun styleApplierClassName(): ClassName {
        return ClassName.get(elementPackageName, String.format(ParisProcessor.STYLE_APPLIER_CLASS_NAME_FORMAT, elementName))
    }

    companion object {

        fun fromEnvironment(roundEnv: RoundEnvironment, elementUtils: Elements, typeUtils: Types,
                            resourceScanner: AndroidResourceScanner,
                            classesToStyleableFieldInfo: Map<Element, List<StyleableFieldInfo>>,
                            classesToBeforeStyleInfo: Map<Element, List<BeforeStyleInfo>>,
                            classesToAfterStyleInfo: Map<Element, List<AfterStyleInfo>>,
                            classesToAttrsInfo: Map<Element, List<AttrInfo>>): List<StyleableInfo> {
            return roundEnv.getElementsAnnotatedWith(Styleable::class.java)
                    .mapNotNull {
                        try {
                            fromElement(elementUtils, typeUtils, resourceScanner, it as TypeElement,
                                    classesToStyleableFieldInfo[it] ?: emptyList(),
                                    classesToBeforeStyleInfo[it] ?: emptyList(),
                                    classesToAfterStyleInfo[it] ?: emptyList(),
                                    classesToAttrsInfo[it] ?: emptyList())
                        } catch(e: ProcessorException) {
                            Errors.log(e)
                            null
                        }
                    }
        }

        @Throws(ProcessorException::class)
        private fun fromElement(elementUtils: Elements,
                                typeUtils: Types,
                                resourceScanner: AndroidResourceScanner,
                                element: TypeElement,
                                styleableFields: List<StyleableFieldInfo>,
                                beforeStyles: List<BeforeStyleInfo>,
                                afterStyles: List<AfterStyleInfo>,
                                attrs: List<AttrInfo>): StyleableInfo {

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
            check(styleableResourceName.isEmpty() || !(styleableFields.isEmpty() && attrs.isEmpty())) {
                "Do not specify the @Styleable value parameter if no class members are annotated with @Attr"
            }

            return StyleableInfo(
                    styleableFields,
                    beforeStyles,
                    afterStyles,
                    attrs,
                    elementPackageName,
                    elementName,
                    elementType,
                    styleableResourceName,
                    dependencies,
                    styles)
        }
    }
}
