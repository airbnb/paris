package com.airbnb.paris.processor.framework

import com.airbnb.paris.processor.abstractions.XType
import javax.lang.model.type.TypeMirror

open class JavaSkyMemoizer(withSkyProcessor: WithJavaSkyProcessor) : WithJavaSkyProcessor by withSkyProcessor {

    val androidViewClassType: TypeMirror by lazy { AndroidClassNames.VIEW.toTypeMirror() }

    val androidViewClassTypeX: XType by lazy {
        processingEnv.requireType(AndroidClassNames.VIEW)
    }
}