package com.airbnb.paris.processor.framework

import androidx.room.compiler.processing.XType

open class SkyMemoizer(withSkyProcessor: WithSkyProcessor) : WithSkyProcessor by withSkyProcessor {

    val androidViewClassTypeX: XType by lazy {
        processingEnv.requireType(AndroidClassNames.VIEW)
    }
}