package com.airbnb.paris.test

import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.ParisProcessorProvider
import com.google.common.truth.Truth.assert_
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory.javaSource
import com.google.testing.compile.JavaSourcesSubject
import com.google.testing.compile.JavaSourcesSubjectFactory.javaSources
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspArgs
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.Test
import java.io.File
import java.net.URL


/**
 * Since AGP 3.6.0, the class-loader behavior has been modified.
 * Unfortunately Guava (via compile-testing) uses a class-loader based mechanism
 * which is valid on JVM but not supposed to be supported on Android.
 * As the project paths are simple enough, we can hardcode them for now.
 */
fun String.patchResource(): URL =
    File("build/intermediates/sourceFolderJavaResources/debug/$this").toURI().toURL()

fun File.unpatchResource(): File = File(canonicalPath.replace("build/intermediates/sourceFolderJavaResources/debug/", "src/test/resources/"))

class ParisProcessorTest : ResourceTest() {

    override fun compilationDelegate(sourceFiles: List<SourceFile>, useKsp: Boolean, args: MutableMap<String, String>): KotlinCompilation {
        return KotlinCompilation().apply {
            if (useKsp) {
                symbolProcessorProviders = listOf(ParisProcessorProvider())
                kspArgs = args
            } else {
                annotationProcessors = listOf(ParisProcessor())
                kaptArgs = args
            }
            sources = sourceFiles
            inheritClassPath = true
            messageOutputStream = System.out
        }
    }


    private fun assertCase(folder: String) {
        val view = JavaFileObjects.forResource("$folder/input/MyView.java".patchResource())
        val generatedParisClass = JavaFileObjects.forResource("$folder/output/Paris.java".patchResource())
        val generatedStyleApplierClass =
            JavaFileObjects.forResource("$folder/output/MyViewStyleApplier.java".patchResource())


        assert_().about(javaSource())
            .that(view)
            .addKotlinGeneratedOption()
            .processedWith(ParisProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedParisClass)
            .and()
            .generatesSources(generatedStyleApplierClass)

        expectSuccessfulGeneration(compilationMode = CompilationMode.KSP)
    }

    private fun assertCaseWithInput(folder: String, input: List<String>, output: List<String>) {
        val inputFileObjects = input.map { JavaFileObjects.forResource("$folder/input/${it}".patchResource()) }
        val outputFileObjects = output.map { JavaFileObjects.forResource("$folder/output/${it}".patchResource()) }

        assert_().about(javaSources())
            .that(inputFileObjects)
            .addKotlinGeneratedOption()
            .processedWith(ParisProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(outputFileObjects.first(), *outputFileObjects.drop(1).toTypedArray())

        expectSuccessfulGeneration(compilationMode = CompilationMode.KSP)
    }

    private fun assertError(
        folder: String,
        errorCount: Int? = null,
        errorFragment: String? = null,
        testKsp: Boolean = true
    ) {
        val view = JavaFileObjects.forResource("$folder/input/MyView.java".patchResource())

        assert_().about(javaSource())
            .that(view)
            .addKotlinGeneratedOption()
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

        if (testKsp) {
            expectCompilationFailure(failureMessage = errorFragment ?: "", compilationMode = CompilationMode.KSP)
        }
    }


    private fun assertErrorWithInput(
        folder: String,
        errorCount: Int? = null,
        errorFragment: String? = null,
        input: List<String>
    ) {
        val inputFileObjects = input.map { JavaFileObjects.forResource("$folder/input/${it}".patchResource()) }

        assert_().about(javaSources())
            .that(inputFileObjects)
            .addKotlinGeneratedOption()
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

        expectCompilationFailure(failureMessage = errorFragment ?: "", compilationMode = CompilationMode.KSP)
    }

    fun JavaSourcesSubject.SingleSourceAdapter.addKotlinGeneratedOption(): JavaSourcesSubject = withCompilerOptions("-Akapt.kotlin.generated=foo")
    fun JavaSourcesSubject.addKotlinGeneratedOption(): JavaSourcesSubject = withCompilerOptions("-Akapt.kotlin.generated=foo")

    @Test
    fun at_style_style_field() {
        assertCase("at_style_style_field")
    }

    @Test
    fun attr_requires_api() {
        assertCase("attr_requires_api")
    }

    @Test
    fun attr_requires_api_default_value() {
        assertCase("attr_requires_api_default_value")
    }

    @Test
    fun attrs() {
        assertCase("attrs")
    }

    @Test
    fun attrs_r_class_import_as_type_alias() = expectSuccessfulGeneration()

    @Test
    fun attrs_r_class_import_fully_qualified() = expectSuccessfulGeneration()

    @Test
    fun default_values() {
        assertCase("default_values")
    }

    @Test
    fun empty_default_style() {
        assertCaseWithInput(
            "empty_default_style",
            listOf("MyViewWithoutStyle.java", "PackageInfo.java"),
            listOf("MyViewWithoutStyleStyleApplier.java", "Paris.java")
        )
    }

    @Test
    fun error_attr_non_res_default_value() {
        // An @Attr with an arbitrary int default value instead of a resource ID

    // don't currently support hardcoded value

//        assertError(
//            "error_attr_non_res_default_value",
//            1,
//            "Could not retrieve Android resource ID from annotation."
//        )
    }

    @Test
    fun error_attr_non_res_value() {
        // An @Attr with an arbitrary int value instead of a resource ID
        // don't currently support hardcoded value

//        assertError(
//            "error_attr_non_res_value",
//            1,
//            "Could not retrieve Android resource ID from annotation."
//        )
    }

    @Test
    fun errorAttrWrongDefaultValueType() {
        // An @Attr with an non-existent R.styleable field

        // Compiler seems to fail on missing symbol...
//        assertError(
//            "error_attr_wrong_default_value_type",
//            1,
//            "Incorrectly typed @Attr defaultValue parameter"
//        )
    }

    @Test
    fun error_attr_wrong_value_type() {
        // An @Attr with an non-existent R.styleable field
        assertError(
            "error_attr_wrong_value_type",
            2,
            "Incorrectly typed @Attr value parameter"
        )
    }

    @Test
    fun error_no_default_style() {
        assertErrorWithInput(
            "error_no_default_style",
            1,
            "No default style found for MyViewWithoutStyle.",
            listOf("MyViewWithoutStyle.java", "PackageInfo.java")
        )
    }

    @Test
    fun error_non_final_style_field() {
        // A non-final field annotated with @Style
        assertError(
            "error_non_final_style_field",
            1,
            "Fields annotated with @Style must be final."
        )
    }

    @Test
    fun error_non_static_style_field() {
        // A non-static field annotated with @Style
        assertError(
            "error_non_static_style_field",
            1,
            "Fields annotated with @Style must be static."
        )
    }

    @Test
    fun error_styleable_child_wrong_value_type() {
        // A @StyleableChild with an non-existent R.styleable field
        assertError(
            "error_styleable_child_wrong_value_type",
            2,
            "Incorrectly typed @StyleableChild value parameter"
        )
    }

    @Test
    fun error_styleable_outside_package_no_R() {
        // A @Styleable view in an unexpected package (outside the package namespace of the module)
        // with no R (or R2) references as annotation parameters. Paris has no way of finding the R
        // package (which it needs to figure out the package of the generated Paris class) so this
        // should cause an error
        assertError(
            folder = "error_styleable_outside_package_no_R",
            errorCount = 1,
            errorFragment = "R class",
            // Ksp testing seems to pull in the package config from the main sources, so we can't test compilation without it :/
            testKsp = false
        )
    }

    @Test
    fun error_styleable_outside_package_with_attr_and_namespaced_resources() {
        // A @Styleable view in an unexpected package (outside the package namespace of the module)
        // with an attribute reference to an r file. If namespaced resources is turned on this will fail, like [errorStyleableOutsidePackageNoR]
        // If namespaced resource is turned off, it will pass as asserted in [styleableOutsidePackageSingleAttr]
        assertErrorWithInput(
            "error_styleable_outside_package_with_attr_and_namespaced_resources",
            1,
            "R class",
            input = listOf("MyView.java", "PackageInfo.java")
        )
    }

    @Test
    fun error_style_field_invalid_type() {
        // A @Style field with an invalid type (not a style)
        assertError(
            "error_style_field_invalid_type",
            1,
            "Fields annotated with @Style must implement com.airbnb.paris.styles.Style or be of type int (and refer to a style resource)."
        )
    }

    @Test
    fun error_two_default_styles() {
        // One @Style named "defaultStyle" and another declared as isDefault = true
        assertError(
            "error_two_default_styles",
            1,
            "Naming a linked style \"default\" and annotating another with @Style(isDefault = true) is invalid."
        )
    }

    @Test
    fun styleable_fields() {
        assertCase("styleable_fields")
    }

    @Test
    fun styleable_in_other_module_single_attr() {
        assertCaseWithInput(
            "styleable_in_other_module_single_attr",
            input = listOf("MyView.java", "PackageInfo.java"),
            output = listOf("MyViewStyleApplier.java", "Paris.java")
        )
    }

    @Test
    fun styleable_minimal() {
        // A @Styleable view with no other annotations used
        assertCase("styleable_minimal")
    }

    @Test
    fun styleable_outside_package_single_attr() {
        // A @Styleable view in an unexpected package (outside the package namespace of the module)
        // and a single @Attr method
        assertCase("styleable_outside_package_single_attr")
    }

    @Test
    fun styles() {
        assertCase("styles")
    }

    @Test
    fun style_extension_generation() = expectSuccessfulGeneration()

    @Test
    fun style_extension_generation_jvm_static() = expectSuccessfulGeneration()

    @Test
    fun style_extension_generation_java() = expectSuccessfulGeneration()

    @Test
    fun overridden_protected_function() = expectSuccessfulGeneration()

    @Test
    fun style_in_kotlin_companion_object() = expectSuccessfulGeneration()
}

