package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceScanner
import com.airbnb.paris.processor.utils.Errors
import com.airbnb.paris.processor.utils.ProcessorException
import com.airbnb.paris.processor.utils.className
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types


class ParisProcessor : AbstractProcessor() {

    companion object {
        internal val PARIS_PACKAGE_NAME = "com.airbnb.paris"
        internal val PARIS_CLASS_NAME = "$PARIS_PACKAGE_NAME.Paris".className()
        internal val STYLE_APPLIER_CLASS_NAME_FORMAT = "%sStyleApplier"

        internal val BUILT_IN_STYLE_APPLIERS = mapOf(
                Pair("com.airbnb.paris.ViewStyleApplier", "android.view.View"),
                Pair("com.airbnb.paris.TextViewStyleApplier", "android.widget.TextView"))

        private val supportedAnnotations: Set<Class<out Annotation>>
            get() {
                return setOf(Styleable::class.java, Attr::class.java)
            }
    }

    private val resourceScanner = AndroidResourceScanner()

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

        if (!styleablesInfo.isEmpty()) {
            try {
                ParisWriter.writeFrom(filer, styleablesInfo)
                Proust.writeFrom(filer, typeUtils, styleablesInfo)
            } catch (e: ProcessorException) {
                Errors.log(e)
            }
        }

        if (roundEnv.processingOver()) {
            Errors.printLoggedErrorsIfAny(messager)
        }

        return true
    }
}
