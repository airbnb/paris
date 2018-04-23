package com.airbnb.paris.processor.android_resource_scanner

import com.airbnb.paris.processor.framework.AndroidClassNames
import com.airbnb.paris.processor.framework.JavaCodeBlock
import com.airbnb.paris.processor.framework.KotlinCodeBlock
import com.airbnb.paris.processor.framework.toKPoet
import com.squareup.javapoet.ClassName

internal class AndroidResourceId {

    val value: Int
    val resourceName: String?
    val code: JavaCodeBlock
    val kotlinCode: KotlinCodeBlock
    val className: ClassName?

    constructor(value: Int) {
        this.value = value
        this.resourceName = null
        this.code = JavaCodeBlock.of("\$L", value)
        this.kotlinCode = KotlinCodeBlock.of("%L", value)
        this.className = null
    }

    constructor(value: Int, className: ClassName, resourceName: String) {
        this.value = value
        this.resourceName = resourceName
        this.code = if (className.topLevelClassName() == AndroidClassNames.R) {
            JavaCodeBlock.of("\$L.\$N", className, resourceName)
        } else {
            JavaCodeBlock.of("\$T.\$N", className, resourceName)
        }
        this.kotlinCode = if (className.topLevelClassName() == AndroidClassNames.R) {
            KotlinCodeBlock.of("%L.%N", className.toKPoet(), resourceName)
        } else {
            KotlinCodeBlock.of("%T.%N", className.toKPoet(), resourceName)
        }
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