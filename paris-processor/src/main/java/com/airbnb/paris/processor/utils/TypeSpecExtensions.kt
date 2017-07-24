package com.airbnb.paris.processor.utils

import com.grosner.kpoet.TypeMethod
import com.squareup.javapoet.TypeSpec

inline operator fun TypeSpec.Builder.invoke(function: TypeMethod) = this.function()
