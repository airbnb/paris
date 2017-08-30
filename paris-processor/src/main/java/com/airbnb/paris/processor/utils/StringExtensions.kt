package com.airbnb.paris.processor.utils

import com.squareup.javapoet.ClassName

fun String.className(): ClassName =
        ClassName.get(this.substringBeforeLast("."), this.substringAfterLast("."))
