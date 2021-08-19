package com.airbnb.paris.processor.framework

import androidx.room.compiler.processing.XType
import javax.lang.model.type.TypeMirror

open class JavaSkyMemoizer(withSkyProcessor: WithJavaSkyProcessor) : WithJavaSkyProcessor by withSkyProcessor {

    val androidViewClassType: TypeMirror by lazy { AndroidClassNames.VIEW.toTypeMirror() }

    val androidViewClassTypeX: XType by lazy {
        processingEnv.requireType(AndroidClassNames.VIEW)
    }
}