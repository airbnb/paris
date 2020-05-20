package com.airbnb.paris.processor.models

import androidx.annotation.RequiresApi
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

        val targetType = element.parameters.firstOrNull()?.asType() ?: run {
            logError(element) {
                "Method with @Attr must provide a single parameter"
            }
            return null
        }

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
        val javadoc = JavaCodeBlock.of("@see \$T#\$N(\$T)\n", enclosingElement, name, targetType)
        // internal functions have a '$' in their name which creates a kdoc error. We could escape it but the part after the '$' is meant for
        // obfuscation anyway so not using it should result in clearer documentation.
        val kdocName = name.substringBefore('$')
        val kdoc = KotlinCodeBlock.of("@see %T.%N\n", enclosingElement, kdocName)

        // We rely on the `RequiresApi` Android annotation to disable certain attributes based on the Android SDK version.
        // 1 is the default since that's the minimum version.
        val requiresApi = element.getAnnotation(RequiresApi::class.java)?.let {
            // value is an alias of api, so we give precedence to api.
            if (it.api > 1) it.api else it.value
        } ?: 1

        return AttrInfo(
            element,
            targetType,
            targetFormat,
            styleableResId,
            defaultValueResId,
            javadoc,
            kdoc,
            requiresApi
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
    val kdoc: KotlinCodeBlock,
    val requiresApi: Int
) : SkyMethodModel(element)
