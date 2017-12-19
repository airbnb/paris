package com.airbnb.paris.processor.framework

import javax.annotation.processing.*
import javax.lang.model.util.*

abstract class SkyProcessor : AbstractProcessor() {

    lateinit var filer: Filer
    lateinit var messager: Messager
    lateinit var elements: Elements
    lateinit var types: Types

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        messager = processingEnv.messager
        elements = processingEnv.elementUtils
        types = processingEnv.typeUtils
    }
}
