package com.airbnb.paris.processor.android_resource_scanner

import com.airbnb.paris.processor.utils.ClassNames
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock

internal class AndroidResourceId {

    val value: Int
    val resourceName: String?
    val code: CodeBlock
    val className: ClassName?

    constructor(value: Int) {
        this.value = value
        this.resourceName = null
        this.code = CodeBlock.of("\$L", value)
        this.className = null
    }

    constructor(value: Int, className: ClassName, resourceName: String) {
        this.value = value
        this.resourceName = resourceName
        this.code = if (className.topLevelClassName() == ClassNames.ANDROID_R)
            CodeBlock.of("\$L.\$N", className, resourceName)
        else
            CodeBlock.of("\$T.\$N", className, resourceName)
        this.className = className
    }

    override fun equals(other: Any?): Boolean {
        return other is AndroidResourceId && value == other.value
    }

    override fun hashCode(): Int {
        return value
    }

    override fun toString(): String {
        throw UnsupportedOperationException("Please use value or code explicitly")
    }
}