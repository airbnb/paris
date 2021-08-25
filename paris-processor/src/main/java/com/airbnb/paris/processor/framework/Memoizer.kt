package com.airbnb.paris.processor.framework

import androidx.room.compiler.processing.XRawType
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.paris.processor.PROXY_CLASS_NAME
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_CLASS_NAME

class Memoizer(processor: ParisProcessor) : JavaSkyMemoizer(processor) {

    val proxyClassType: XType by lazy { processor.environment.requireType(PROXY_CLASS_NAME) }

    val styleClassTypeX: XType by lazy { processor.environment.requireType(STYLE_CLASS_NAME) }

    val rStyleTypeElementX: XTypeElement? by lazy {
        val rElement = processor.RElement ?: error("R Class not found")
        processor.environment.findType("${rElement.qualifiedName}.style")?.typeElement
    }
}