package com.airbnb.paris.processor.framework

import androidx.room.compiler.processing.XType
import com.airbnb.paris.processor.BaseProcessor

open class JavaSkyMemoizer(val processor: BaseProcessor) {

    val androidViewClassTypeX: XType by lazy {
        processor.environment.requireType(AndroidClassNames.VIEW)
    }
}