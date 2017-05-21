package com.airbnb.paris.processor.utils

import com.squareup.javapoet.ClassName

fun String.className(): ClassName {
    return ClassName.get(this.substringBeforeLast("."), this.substringAfterLast("."))
}