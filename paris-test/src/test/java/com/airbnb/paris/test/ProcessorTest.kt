package com.airbnb.paris.test

import com.airbnb.paris.processor.ParisProcessor
import com.google.common.truth.Truth.assert_
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory.javaSource
import org.junit.Test



class ProcessorTest {

    @Test
    fun basic() {
        val model = JavaFileObjects
                .forResource("MyView.java")

//        val generatedModel = JavaFileObjects.forResource("MyViewStyle.java")

        assert_().about(javaSource())
                .that(model)
                .processedWith(ParisProcessor())
                .compilesWithoutError()
    }
}