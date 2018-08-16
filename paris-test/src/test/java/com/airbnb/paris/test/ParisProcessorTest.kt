package com.airbnb.paris.test

import com.airbnb.paris.processor.ParisProcessor
import com.google.common.truth.Truth.assert_
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory.javaSource
import org.junit.Test


class ParisProcessorTest {

    private fun assertCase(folder: String) {
        val view = JavaFileObjects.forResource("$folder/MyView.java")
        val generatedParisClass = JavaFileObjects.forResource("$folder/Paris.java")
        val generatedStyleApplierClass =
            JavaFileObjects.forResource("$folder/MyViewStyleApplier.java")

        assert_().about(javaSource())
            .that(view)
            .processedWith(ParisProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedParisClass)
            .and()
            .generatesSources(generatedStyleApplierClass)
    }

    private fun assertError(
        folder: String,
        errorCount: Int? = null,
        errorFragment: String? = null
    ) {
        val view = JavaFileObjects.forResource("$folder/MyView.java")

        assert_().about(javaSource())
            .that(view)
            .processedWith(ParisProcessor())
            .failsToCompile()
            .apply {
                errorCount?.let {
                    withErrorCount(it)
                }
                errorFragment?.let {
                    withErrorContaining(it)
                }
            }
    }

    @Test
    fun atStyleStyleField() {
        assertCase("at_style_style_field")
    }

    @Test
    fun attrs() {
        assertCase("attrs")
    }

    @Test
    fun defaultValues() {
        assertCase("default_values")
    }

    @Test
    fun errorAttrNonResDefaultValue() {
        // An @Attr with an arbitrary int default value instead of a resource ID
        assertError(
            "error_attr_non_res_default_value",
            1,
            "Could not retrieve Android resource ID from annotation."
        )
    }

    @Test
    fun errorAttrNonResValue() {
        // An @Attr with an arbitrary int value instead of a resource ID
        assertError(
            "error_attr_non_res_value",
            1,
            "Could not retrieve Android resource ID from annotation."
        )
    }

    @Test
    fun errorAttrWrongDefaultValueType() {
        // An @Attr with an non-existent R.styleable field
        assertError(
            "error_attr_wrong_default_value_type",
            2,
            "Incorrectly typed @Attr defaultValue parameter"
        )
    }

    @Test
    fun errorAttrWrongValueType() {
        // An @Attr with an non-existent R.styleable field
        assertError(
            "error_attr_wrong_value_type",
            2,
            "Incorrectly typed @Attr value parameter"
        )
    }

    @Test
    fun errorNonFinalStyleField() {
        // A non-final field annotated with @Style
        assertError(
            "error_non_final_style_field",
            1,
            "Fields annotated with @Style must be final."
        )
    }

    @Test
    fun errorNonStaticStyleField() {
        // A non-static field annotated with @Style
        assertError(
            "error_non_static_style_field",
            1,
            "Fields annotated with @Style must be static."
        )
    }

    @Test
    fun errorPrivateStyleField() {
        // A private field annotated with @Style
        assertError(
            "error_private_style_field",
            1,
            "Fields annotated with @Style can't be private or protected."
        )
    }

    @Test
    fun errorStyleableChildWrongValueType() {
        // A @StyleableChild with an non-existent R.styleable field
        assertError(
            "error_styleable_child_wrong_value_type",
            2,
            "Incorrectly typed @StyleableChild value parameter"
        )
    }

    @Test
    fun errorStyleableOutsidePackageNoR() {
        // A @Styleable view in an unexpected package (outside the package namespace of the module)
        // with no R (or R2) references as annotation parameters. Paris has no way of finding the R
        // package (which it needs to figure out the package of the generated Paris class) so this
        // should cause an error
        assertError("error_styleable_outside_package_no_R", 1, "R class")
    }

    @Test
    fun errorStyleFieldInvalidType() {
        // A @Style field with an invalid type (not a style)
        assertError(
            "error_style_field_invalid_type",
            1,
            "Fields annotated with @Style must implement com.airbnb.paris.styles.Style or be of type int (and refer to a style resource)."
        )
    }

    @Test
    fun errorTwoDefaultStyles() {
        // One @Style named "defaultStyle" and another declared as isDefault = true
        assertError(
            "error_two_default_styles",
            1,
            "Naming a linked style \"default\" and annotating another with @Style(isDefault = true) is invalid."
        )
    }

    @Test
    fun styleableFields() {
        assertCase("styleable_fields")
    }

    @Test
    fun styleableMinimal() {
        // A @Styleable view with no other annotations used
        assertCase("styleable_minimal")
    }

    @Test
    fun styleableOutsidePackageSingleAttr() {
        // A @Styleable view in an unexpected package (outside the package namespace of the module)
        // and a single @Attr method
        assertCase("styleable_outside_package_single_attr")
    }

    @Test
    fun styles() {
        assertCase("styles")
    }
}