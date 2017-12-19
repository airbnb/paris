package com.airbnb.paris.test

import com.airbnb.paris.processor.*
import com.google.common.truth.Truth.*
import com.google.testing.compile.*
import com.google.testing.compile.JavaSourceSubjectFactory.*
import org.junit.*



class ParisProcessorTest {

    fun assertCase(folder: String) {
        val view = JavaFileObjects.forResource("$folder/MyView.java")
        val generatedParisClass = JavaFileObjects.forResource("$folder/Paris.java")
        val generatedStyleApplierClass = JavaFileObjects.forResource("$folder/MyViewStyleApplier.java")

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
    fun attrs() {
        // TODO Add Drawable case
        assertCase("attrs")
    }

    @Test
    fun defaultValues() {
        assertCase("default_values")
    }

    @Test
    fun styleableFields() {
        assertCase("styleable_fields")
    }

    @Test
    fun styles() {
        assertCase("styles")
    }
}