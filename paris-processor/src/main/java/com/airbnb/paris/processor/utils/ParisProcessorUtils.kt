package com.airbnb.paris.processor.utils

// This is purposefully left public to facilitate integrations with Paris
// TODO BREAKING Refactor this to be an object with @JvmStatic functions
class ParisProcessorUtils {

    companion object {
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
            // Converts any name to CamelCase
            val isNameAllCaps = name.all { it.isUpperCase() || !it.isLetter() }
            return name
                .foldRightIndexed("") { index, c, acc ->
                    if (c == '_') {
                        acc
                    } else {
                        if (index == 0) {
                            c.uppercaseChar() + acc
                        } else if (name[index - 1] != '_') {
                            if (isNameAllCaps) {
                                c.lowercaseChar() + acc
                            } else {
                                c + acc
                            }
                        } else {
                            c.uppercaseChar() + acc
                        }
                    }
                }
                .let {
                    if (it != "Style") {
                        it.removeSuffix("Style")
                    } else {
                        it
                    }
                }
        }
    }
}
