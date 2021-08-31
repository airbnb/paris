package com.airbnb.paris.test

import com.github.difflib.DiffUtils
import com.google.common.io.Resources
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import org.junit.Rule
import org.junit.rules.TestName
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.doesNotContain
import strikt.assertions.isEmpty
import strikt.assertions.isNotNull
import java.io.File

abstract class ResourceTest {

    @get:Rule
    var testName: TestName = TestName()

    /**
     * If all tests in the the class are placed under a single folder you can omit it from the test name by
     * overriding this value.
     */
    open val rootDir = ""

    /**
     * Delegate compilation to the implementing classes so they can provide their own AnnotationProcessor or
     * KotlinSymbolProcessor (most likely Scabbard) without this module having to depend on that (or those)
     * processor(s).
     */
    abstract fun compilationDelegate(sourceFiles: List<SourceFile>, useKsp: Boolean, args: MutableMap<String, String>): KotlinCompilation

    /**
     * Allows writing a test name with backticks that specifies a resource folder. If there folder is nested `/` should be
     * encoded as ` `.
     */
    fun expectSuccessfulGeneration(
        args: MutableMap<String, String> = mutableMapOf(),
        unexpectedFileNames: List<String> = emptyList(),
        compilationMode: CompilationMode = CompilationMode.ALL
    ) {
        val folderString = inferResourceFolderFromTest()
        val input = getSourceFiles("$folderString/input")
        val outputSources = getSourceFiles("$folderString/output")
        val output = getFilesFromResources("$folderString/output")

        if (compilationMode.testKSP) {
            testCodeGeneration(
                // If any output sources reference each other compilation of them fails unless they are included in sources explicitly.
                sourceFiles = input + outputSources,
                expectedOutput = output,
                useKsp = true,
                args = args,
                unexpectedOutputFileName = unexpectedFileNames
            )
        }
        if (compilationMode.testKapt) {
            testCodeGeneration(
                sourceFiles = input,
                expectedOutput = output,
                useKsp = false,
                args = args,
                unexpectedOutputFileName = unexpectedFileNames
            )
        }
    }

    /**
     * Allows writing compilation test name with backticks that specifies a resource folder. If there folder is nested `/` should be
     * encoded as ` `. Compilation is expceted to fail with [failureMessage].
     */
    fun expectCompilationFailure(
        failureMessage: String,
        args: MutableMap<String, String> = mutableMapOf(),
        compilationMode: CompilationMode = CompilationMode.ALL
    ) {
        val folderString = inferResourceFolderFromTest()
        val input = getSourceFiles("$folderString/input")
        if (compilationMode.testKSP) {
            testCodeGenerationFailure(
                sourceFiles = input,
                failureMessage = failureMessage,
                useKsp = true,
                args = args
            )
        }
        if (compilationMode.testKapt) {
            testCodeGenerationFailure(
                sourceFiles = input,
                failureMessage = failureMessage,
                useKsp = false,
                args = args
            )
        }
    }

    private fun getFilesFromResources(folderString: String): List<File> {
        val resourceDirectory = File(folderString.patchResource().path)
        return resourceDirectory.walk().maxDepth(1).filter { it.isFile }.toList()
    }

    /**
     * For test modules that are Android modules, we have to encode .kt and .java files as .txt resources for them to
     * be included in the test environment as Resources. This method creates a list of SourceFiles by reading the lines
     * of any .kt.txt, .java.txt, .kt, or .java files and reconstructing the files as .java or .kt files if necessary.
     */
    private fun getSourceFiles(folderString: String): List<SourceFile> {
        return getFilesFromResources(folderString).map {
            if (it.name.endsWith("kt.txt")) {
                SourceFile.kotlin(it.name.removeSuffix(".txt"), it.readText())
            } else if (it.name.endsWith("java.txt")) {
                SourceFile.java(it.name.removeSuffix(".txt"), it.readText())
            } else {
                SourceFile.Companion.fromPath(it)
            }
        }
    }

    private fun inferResourceFolderFromTest(): String {
        return rootDir + testName.methodName.replace(" ", "/")
    }

    /**
     * Test that [sourceFiles] generate [expectedOutput].
     * @param useKsp - If true ksp will be used as the annotation processing backend, if false, kapt will be used.
     * @param updateDifferences - If true, if compilation is successful, but the output is different, the generated output
     * will be return in resources in `/build`.
     *
     * You can set [UPDATE_TEST_SOURCES_ON_DIFF] to true to have the original sources file updated for the actual generated code.
     */
    fun testCodeGeneration(
        sourceFiles: List<SourceFile>,
        expectedOutput: List<File> = emptyList(),
        unexpectedOutputFileName: List<String> = emptyList(),
        useKsp: Boolean = true,
        args: MutableMap<String, String> = mutableMapOf()
    ) {
        println("Using ksp: $useKsp")
        val compilation = compilationDelegate(sourceFiles, useKsp, args)
        val result = compilation.compile()

        val generatedSources = if (useKsp) {
            compilation.kspSourcesDir.walk().filter { it.isFile }.toList()
        } else {
            result.sourcesGeneratedByAnnotationProcessor
        }

        if (result.exitCode != KotlinCompilation.ExitCode.OK) {
            println("Generated:")
            generatedSources.forEach { println(it.readText()) }
            error("Compilation failed with ${result.exitCode}.")
        }

        println("Generated files:")
        generatedSources.forEach { println(it.name) }

        expect {
            expectedOutput.forEach { expectedOutputFile ->
                val actualOutputFileName = expectedOutputFile.name
                // Since we may encode output files as txt resources, we need to remove the suffix when comparing
                // generated filename to expected filename.
                val expectedOutputFilename = actualOutputFileName.removeSuffix(".txt")
                val generated = generatedSources.find { it.name == expectedOutputFilename }
                that(generated) {
                    isNotNull().and {
                        val patch = DiffUtils.diff(generated!!.readLines(), expectedOutputFile.readLines())
                        if (patch.deltas.isNotEmpty()) {
                            println("Found differences for $expectedOutputFilename!")
                            println("Actual filename in filesystem is $actualOutputFileName")
                            println("Expected:\n")
                            println(expectedOutputFile.readText())
                            println("Generated:\n")
                            println(generated.readText())

                            println("Expected source is at: ${expectedOutputFile.unpatchResource()}")
                            val actualFile = File(expectedOutputFile.parent, "actual/${expectedOutputFile.name}").apply {
                                parentFile?.mkdirs()
                                writeText(generated.readText())
                            }
                            println("Actual source is at: $actualFile")
                            if (UPDATE_TEST_SOURCES_ON_DIFF) {
                                println("UPDATE_TEST_SOURCES_ON_DIFF is enabled; updating expected sources with actual sources.")
                                expectedOutputFile.unpatchResource().writeText(generated.readText())
                            }
                        }
                        that(patch.deltas).isEmpty()
                    }
                }.describedAs(expectedOutputFilename)
            }
        }
        val generatedFileNames = generatedSources.map { it.name }
        if (unexpectedOutputFileName.isNotEmpty()) {
            expectThat(generatedFileNames).doesNotContain(unexpectedOutputFileName)
        }
    }

    fun testCodeGenerationFailure(
        sourceFiles: List<SourceFile>,
        failureMessage: String,
        args: MutableMap<String, String>,
        useKsp: Boolean = true,
    ) {
        val compilation = compilationDelegate(sourceFiles, useKsp, args)
        val result = compilation.compile()

        if (result.exitCode == KotlinCompilation.ExitCode.OK) {
            error("Compilation succeed.")
        }
        expectThat(result.messages).contains(failureMessage)
    }
}

/**
 * Change to true to have tests auto update the expected sources files for easy updating of tests.
 */
const val UPDATE_TEST_SOURCES_ON_DIFF = false
