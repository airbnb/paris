package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.processor.Format
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.framework.models.SkyMethodModel
import com.airbnb.paris.processor.framework.models.SkyMethodModelFactory
import com.airbnb.paris.processor.getResourceId
import java.lang.annotation.AnnotationTypeMismatchException
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

internal class AttrInfoExtractor
    : SkyMethodModelFactory<AttrInfo>(Attr::class.java) {

    override fun elementToModel(element: ExecutableElement): AttrInfo? {
        if (element.isPrivate() || element.isProtected()) {
            logError(element) {
                "Methods annotated with @Attr can't be private or protected."
            }
            return null
        }

        val attr = element.getAnnotation(Attr::class.java)

        val targetType = element.parameters[0].asType()

        val targetFormat = Format.forElement(element)

        val styleableResId: AndroidResourceId
        try {
            styleableResId = getResourceId(Attr::class.java, element, attr.value) ?: return null
        } catch (e: AnnotationTypeMismatchException) {
            logError(element) {
                "Incorrectly typed @Attr value parameter. (This usually happens when an R value doesn't exist.)"
            }
            return null
        }

        var defaultValueResId: AndroidResourceId? = null
        if (attr.defaultValue != -1) {
            try {
                defaultValueResId = getResourceId(Attr::class.java, element, attr.defaultValue) ?: return null
            } catch (e: AnnotationTypeMismatchException) {
                logError(element) {
                    "Incorrectly typed @Attr defaultValue parameter. (This usually happens when an R value doesn't exist.)"
                }
                return null
            }
        }

        val enclosingElement = element.enclosingElement as TypeElement
        val name = element.simpleName.toString()
        val javadoc = JavaCodeBlock.of("@see \$T#\$N(\$T)", enclosingElement, name, targetType)
        val kdoc = KotlinCodeBlock.of("@see %T.%N", enclosingElement, name)

        return AttrInfo(
            element,
            targetType,
            targetFormat,
            styleableResId,
            defaultValueResId,
            javadoc,
            kdoc
        )
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
    val javadoc: JavaCodeBlock,
    val kdoc: KotlinCodeBlock
) : SkyMethodModel(element)
