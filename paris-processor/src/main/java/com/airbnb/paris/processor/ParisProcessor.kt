package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types


class ParisProcessor : AbstractProcessor() {

    companion object {
        internal val supportedAnnotations: Set<Class<out Annotation>>
            get() {
                return setOf(Styleable::class.java, Attr::class.java)
            }
    }

    private val resourceScanner = ResourceScanner()

    private lateinit var filer: Filer
    private lateinit var messager: Messager
    private lateinit var elementUtils: Elements
    private lateinit var typeUtils: Types

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        resourceScanner.init(processingEnv)
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
        val classesToAttrsInfo: Map<Element, List<AttrInfo>> = AttrInfo.fromEnvironment(roundEnv, elementUtils, typeUtils, resourceScanner)
                .groupBy { it.enclosingElement }
        val styleablesInfo: List<StyleableInfo> = StyleableInfo.fromEnvironment(roundEnv, resourceScanner, classesToAttrsInfo)

        val styleableClassesTree = StyleableClassesTree(typeUtils, styleablesInfo)

        try {
            ParisWriter.writeFrom(filer, styleablesInfo)
            Proust.writeFrom(filer, typeUtils, styleablesInfo, styleableClassesTree)
        } catch (e: ProcessorException) {
            Errors.log(e)
        }

        if (roundEnv.processingOver()) {
            Errors.printLoggedErrorsIfAny(messager)
        }

        return true
    }
}
