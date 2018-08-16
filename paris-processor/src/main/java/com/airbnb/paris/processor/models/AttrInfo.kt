package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.processor.Format
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.WithParisProcessor
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.framework.JavaCodeBlock
import com.airbnb.paris.processor.framework.KotlinCodeBlock
import com.airbnb.paris.processor.framework.isPrivate
import com.airbnb.paris.processor.framework.isProtected
import com.airbnb.paris.processor.framework.models.SkyMethodModel
import com.airbnb.paris.processor.framework.models.SkyMethodModelFactory
import java.lang.annotation.AnnotationTypeMismatchException
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

internal class AttrInfoExtractor(
    override val processor: ParisProcessor
) : SkyMethodModelFactory<AttrInfo>(processor, Attr::class.java), WithParisProcessor {

    override fun elementToModel(element: ExecutableElement): AttrInfo? {
        if (element.isPrivate() || element.isProtected()) {
            logError(element) {
                "Methods annotated with @Attr can't be private or protected."
            }
            return null
        }

        val attr = element.getAnnotation(Attr::class.java)

        val targetType = element.parameters[0].asType()

        val targetFormat = Format.forElement(processor, element)

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
        try {
            if (attr.defaultValue != -1) {
                defaultValueResId = getResourceId(Attr::class.java, element, attr.defaultValue) ?: return null
            }
        } catch (e: AnnotationTypeMismatchException) {
            logError(element) {
                "Incorrectly typed @Attr defaultValue parameter. (This usually happens when an R value doesn't exist.)"
            }
            return null
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
