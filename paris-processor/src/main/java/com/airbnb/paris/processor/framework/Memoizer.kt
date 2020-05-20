package com.airbnb.paris.processor.framework

import com.airbnb.paris.processor.PROXY_CLASS_NAME
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_CLASS_NAME
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

class Memoizer(override val processor: ParisProcessor) : SkyMemoizer(processor) {

    val proxyClassTypeErased: TypeMirror by lazy { erasure(PROXY_CLASS_NAME.toTypeMirror()) }

    val styleClassType: TypeMirror by lazy { STYLE_CLASS_NAME.toTypeMirror() }

    val rStyleTypeElement: TypeElement? by lazy {
        val rElement = processor.RElement ?: error("R Class not found")
        elements.getTypeElement("${rElement.qualifiedName}.style") }
}