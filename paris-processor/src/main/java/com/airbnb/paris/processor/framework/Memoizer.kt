package com.airbnb.paris.processor.framework

import androidx.room.compiler.processing.XRawType
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.paris.processor.PROXY_CLASS_NAME
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_CLASS_NAME
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

class Memoizer(override val processor: ParisProcessor) : JavaSkyMemoizer(processor) {

    val proxyClassTypeErased: TypeMirror by lazy { erasure(PROXY_CLASS_NAME.toTypeMirror()) }
    val proxyClassTypeErasedX: XRawType by lazy { processingEnv.requireType(PROXY_CLASS_NAME).rawType }

    val styleClassType: TypeMirror by lazy { STYLE_CLASS_NAME.toTypeMirror() }
    val styleClassTypeX: XType by lazy { processingEnv.requireType(STYLE_CLASS_NAME) }

    val rStyleTypeElement: TypeElement? by lazy {
        val rElement = processor.RElement ?: error("R Class not found")
        elements.getTypeElement("${rElement.qualifiedName}.style")
    }

    val rStyleTypeElementX: XTypeElement? by lazy {
        val rElement = processor.RElement ?: error("R Class not found")
        processingEnv.findType("${rElement.qualifiedName}.style")?.typeElement
    }
}