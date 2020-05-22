package com.airbnb.paris.processor.framework

import javax.lang.model.type.TypeMirror

open class SkyMemoizer(withSkyProcessor: WithSkyProcessor) : WithSkyProcessor by withSkyProcessor {

    val androidViewClassType: TypeMirror by lazy { AndroidClassNames.VIEW.toTypeMirror() }

}