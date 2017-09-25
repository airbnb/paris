package com.airbnb.paris.processor.utils

class ParisProcessorUtils {

    companion object {

        /**
         * Format the name of a @Style annotated field or method to match what the style applier and
         * builder will use.
         *
         * Examples:
         * MY_RED_STYLE -> MyRedStyle
         * myRedStyle -> MyRedStyle
         */
        @JvmStatic fun reformatStyleFieldOrMethodName(name: String): String {
            // Converts any name to CamelCase
            val isNameAllCaps = name.all { it.isUpperCase() || !it.isLetter() }
            return name.foldRightIndexed("") { index, c, acc ->
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
        }
    }
}
