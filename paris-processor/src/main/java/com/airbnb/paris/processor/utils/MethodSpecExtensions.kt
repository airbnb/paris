package com.airbnb.paris.processor.utils

import com.grosner.kpoet.MethodMethod
import com.squareup.javapoet.MethodSpec

inline operator fun MethodSpec.Builder.invoke(function: MethodMethod) = this.function()
