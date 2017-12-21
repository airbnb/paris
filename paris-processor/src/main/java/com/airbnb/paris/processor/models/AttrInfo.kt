package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.*
import com.airbnb.paris.processor.android_resource_scanner.*
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.utils.*
import com.squareup.javapoet.*
import javax.lang.model.element.*
import javax.lang.model.type.*

internal class AttrInfoExtractor
    : SkyMethodModelFactory<AttrInfo>(Attr::class.java) {

    override fun elementToModel(element: ExecutableElement): AttrInfo? {
        check(element.isNotPrivate() && element.isNotProtected(), element) {
            "Methods annotated with @Attr can't be private or protected"
        }

        val attr = element.getAnnotation(Attr::class.java)

        val targetType = element.parameters[0].asType()

        val targetFormat = Format.forElement(element)

        val styleableResId = getResourceId(Attr::class.java, element, attr.value)
        var defaultValueResId: AndroidResourceId? = null
        if (attr.defaultValue != -1) {
            defaultValueResId = getResourceId(Attr::class.java, element, attr.defaultValue)
        }

        val enclosingElement = element.enclosingElement as TypeElement
        val name = element.simpleName.toString()
        val javadoc = CodeBlock.of("@see \$T#\$N(\$T)", enclosingElement, name, targetType)

        return AttrInfo(
                element,
                targetType,
                targetFormat,
                styleableResId,
                defaultValueResId,
                javadoc)
    }
}

/**
 * Element  The annotated element
 * Target   The method parameter
 */
internal class AttrInfo(
        element: ExecutableElement,
        val targetType: TypeMirror,
        val targetFormat: Format,
        val styleableResId: AndroidResourceId,
        val defaultValueResId: AndroidResourceId?,
        val javadoc: CodeBlock
) : SkyMethodModel(element)
