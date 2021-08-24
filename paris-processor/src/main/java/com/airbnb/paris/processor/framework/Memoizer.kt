package com.airbnb.paris.processor.framework

import androidx.room.compiler.processing.XRawType
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.paris.processor.PROXY_CLASS_NAME
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.STYLE_CLASS_NAME

class Memoizer(override val processor: ParisProcessor) : SkyMemoizer(processor) {

    val proxyClassTypeErasedX: XRawType by lazy { processingEnv.requireType(PROXY_CLASS_NAME).rawType }

    val styleClassTypeX: XType by lazy { processingEnv.requireType(STYLE_CLASS_NAME) }

    val rStyleTypeElementX: XTypeElement? by lazy {
        processingEnv.findType("${processor.RElement.qualifiedName}.style")?.typeElement
    }
}