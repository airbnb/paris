package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable
import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic


// TODO  Support using the same attr on multiple methods/fields
@AutoService(Processor::class)
class ParisProcessor : AbstractProcessor() {

    companion object {

        internal val supportedAnnotations: Set<Class<out Annotation>>
            get() {
                return setOf(
                        Styleable::class.java,
                        Attr::class.java)
            }
    }

    private val loggedExceptions: MutableList<Exception> = ArrayList()
    private val resourceProcessor = ResourceProcessor()

    private lateinit var filer: Filer
    private lateinit var messager: Messager
    private lateinit var elementUtils: Elements
    private lateinit var typeUtils: Types

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        resourceProcessor.init(processingEnv)
        filer = processingEnv.filer
        messager = processingEnv.messager
        elementUtils = processingEnv.elementUtils
        typeUtils = processingEnv.typeUtils
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        val types: MutableSet<String> = LinkedHashSet()
        supportedAnnotations.mapTo(types) { it.canonicalName }
        return types
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val allAttrs: MutableList<AttrInfo> = ArrayList()
        roundEnv.getElementsAnnotatedWith(Attr::class.java)
                .mapTo(allAttrs) {
                    AttrInfo.fromElement(resourceProcessor, it)
                }

        if (allAttrs.isEmpty()) {
            return true
        }
        val rClassName: ClassName = allAttrs[0].id.className.enclosingClassName()

        val styleableClasses: MutableList<StyleableClassInfo> = ArrayList()
        roundEnv.getElementsAnnotatedWith(Styleable::class.java)
                .mapTo(styleableClasses) { element ->
                    val attrs = allAttrs.filter { element == it.enclosingElement }
                    StyleableClassInfo.fromElement(element, attrs)
                }

        try {
            Proust.writeFrom(filer, styleableClasses, rClassName)
        } catch (e: IOException) {
            logError(e)
        }

        if (roundEnv.processingOver()) {
            writeLoggedErrorsIfAny()
        }

        return true
    }


    private fun logError(e: Exception) {
        loggedExceptions.add(e)
    }

    private fun writeLoggedErrorsIfAny() {
        if (loggedExceptions.isEmpty()) {
            return
        }

        // In case of a build failure only the first error is displayed, so this concatenates all of them in one
        // to help with debugging
        val concatenatedErrorsBuilder = StringBuilder()
        val iterator = loggedExceptions.iterator()
        while (iterator.hasNext()) {
            val exception = iterator.next()
            concatenatedErrorsBuilder.append("\n\n").append(exception.toString())
            iterator.remove()
        }
        messager.printMessage(Diagnostic.Kind.ERROR, concatenatedErrorsBuilder.toString())
    }
}
