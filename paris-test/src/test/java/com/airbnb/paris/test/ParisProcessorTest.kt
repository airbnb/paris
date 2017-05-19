package com.airbnb.paris.test

import com.airbnb.paris.processor.ParisProcessor
import com.google.common.truth.Truth.assert_
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory.javaSource
import org.junit.Test



class ParisProcessorTest {

    @Test
    fun basic() {
        val view = JavaFileObjects.forResource("MyView.java")
        val generatedParisClass = JavaFileObjects.forResource("Paris.java")
        val generatedStyleApplierClass = JavaFileObjects.forResource("MyViewStyleApplier.java")

        assert_().about(javaSource())
                .that(view)
                .processedWith(ParisProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedParisClass)
                .and()
                .generatesSources(generatedStyleApplierClass)
    }

    @Test
    fun dependenciesWithNoAttributes() {
        val view = JavaFileObjects.forResource("MyViewDependencyNoAttrs.java")
        val generatedStyleApplierClass = JavaFileObjects.forResource("MyViewDependencyNoAttrsStyleApplier.java")

        assert_().about(javaSource())
                .that(view)
                .processedWith(ParisProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedStyleApplierClass)
    }
}