package com.airbnb.paris.processor.android_resource_scanner

import com.airbnb.paris.processor.framework.AndroidClassNames
import com.airbnb.paris.processor.framework.JavaCodeBlock
import com.airbnb.paris.processor.framework.KotlinCodeBlock
import com.airbnb.paris.processor.framework.toKPoet
import com.squareup.javapoet.ClassName

/**
 * @param className Like com.example.R.styleable
 * @param resourceName Like title_view
 */
class AndroidResourceId(val value: Int, val className: ClassName, val resourceName: String) {

    val rClassName: ClassName = className.topLevelClassName()

    val code: JavaCodeBlock = if (rClassName == AndroidClassNames.R) {
        JavaCodeBlock.of("\$L.\$N", className, resourceName)
    } else {
        JavaCodeBlock.of("\$T.\$N", className, resourceName)
    }

    val kotlinCode: KotlinCodeBlock = if (rClassName == AndroidClassNames.R) {
        KotlinCodeBlock.of("%L.%N", className.toKPoet(), resourceName)
    } else {
        KotlinCodeBlock.of("%T.%N", className.toKPoet(), resourceName)
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
