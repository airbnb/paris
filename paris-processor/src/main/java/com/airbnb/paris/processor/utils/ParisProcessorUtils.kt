package com.airbnb.paris.processor.utils

internal object ParisProcessorUtils {


    /**
     * Format the name of a @Style annotated field or method to match what the style applier and
     * builder will use.
     *
     * "Style" suffixes are removed to make it possible to use in field or method names while
     * avoiding the redundancy of having it be part of style builder methods.
     *
     * Examples:
     * MY_RED -> MyRed
     * myRed -> MyRed
     * MY_RED_STYLE -> MyRed
     * myRedStyle -> MyRed
     */
    @JvmStatic
    fun reformatStyleFieldOrMethodName(name: String): String {
        // TODO What if the whole name is "Style"?
        // Converts any name to CamelCase
        val isNameAllCaps = name.all { it.isUpperCase() || !it.isLetter() }
        return name
            .foldRightIndexed("") { index, c, acc ->
                if (c == '_') {
                    acc
                } else {
                    if (index == 0) {
                        c.toUpperCase() + acc
                    } else if (name[index - 1] != '_') {
                        if (isNameAllCaps) {
                            c.toLowerCase() + acc
                        } else {
                            c + acc
                        }
                    } else {
                        c.toUpperCase() + acc
                    }
                }
            }
            .removeSuffix("Style")
    }

}

internal fun String.lowerCaseFirstLetter(): String {
    if (isEmpty()) {
        return this
    }

    return Character.toLowerCase(get(0)) + substring(1)
}
