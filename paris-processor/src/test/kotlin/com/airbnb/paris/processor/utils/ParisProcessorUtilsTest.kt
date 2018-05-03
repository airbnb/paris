package com.airbnb.paris.processor.utils

import com.airbnb.paris.processor.utils.ParisProcessorUtils.Companion.reformatStyleFieldOrMethodName
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class ParisProcessorUtilsTest : StringSpec({

    "upper snake case style names should be converted to upper camel" {
        reformatStyleFieldOrMethodName("MY_RED") shouldBe "MyRed"
    }

    "lower camel case style names should be converted to upper camel" {
        reformatStyleFieldOrMethodName("myRed") shouldBe "MyRed"
    }

    "upper camel case style names shouldn't change" {
        reformatStyleFieldOrMethodName("MyRed") shouldBe "MyRed"
    }

    "Style suffix should be removed from style names" {
        reformatStyleFieldOrMethodName("MY_RED_STYLE") shouldBe "MyRed"
        reformatStyleFieldOrMethodName("myRedStyle") shouldBe "MyRed"
        reformatStyleFieldOrMethodName("MyRedStyle") shouldBe "MyRed"
    }

    "style whose whole name is style/Style/STYLE doesn't change" {
        reformatStyleFieldOrMethodName("style") shouldBe "Style"
        reformatStyleFieldOrMethodName("Style") shouldBe "Style"
        reformatStyleFieldOrMethodName("STYLE") shouldBe "Style"
    }
})
