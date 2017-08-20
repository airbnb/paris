package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.ParisConfig
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceScanner
import com.airbnb.paris.processor.utils.Errors
import com.airbnb.paris.processor.utils.ProcessorException
import com.airbnb.paris.processor.utils.className
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types


class ParisProcessor : AbstractProcessor() {

    companion object {
        internal const val PARIS_PACKAGE_NAME = "com.airbnb.paris"
        internal const val STYLE_APPLIER_CLASS_NAME_FORMAT = "%sStyleApplier"

        internal val PARIS_BASE_CLASS_NAME = "$PARIS_PACKAGE_NAME.ParisBase".className()
        internal val PARIS_CLASS_NAME = "$PARIS_PACKAGE_NAME.Paris".className()
        internal val STYLE_CLASS_NAME = "$PARIS_PACKAGE_NAME.Style".className()
        internal val STYLE_APPLIER_CLASS_NAME = "$PARIS_PACKAGE_NAME.StyleApplier".className()
        internal val STYLE_APPLIER_UTILS_CLASS_NAME = "$PARIS_PACKAGE_NAME.StyleApplierUtils".className()
        internal val TYPED_ARRAY_WRAPPER_CLASS_NAME = "$PARIS_PACKAGE_NAME.TypedArrayWrapper".className()

        internal val BUILT_IN_STYLE_APPLIERS = mapOf(
                Pair("com.airbnb.paris.ViewStyleApplier", "android.view.View"),
                Pair("com.airbnb.paris.TextViewStyleApplier", "android.widget.TextView"))

        private val supportedAnnotations: Set<Class<out Annotation>> = setOf(Styleable::class.java, Attr::class.java)
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
        var generateParisClass = true;

        val configElement = roundEnv.getElementsAnnotatedWith(ParisConfig::class.java).firstOrNull()
        if (configElement != null) {
            val config = configElement.getAnnotation(ParisConfig::class.java)
            generateParisClass = config.generateParisClass
        }

        val classesToBeforeStyleInfo = BeforeStyleInfo.fromEnvironment(roundEnv)
                .groupBy { it.enclosingElement }
        val classesToAfterStyleInfo = AfterStyleInfo.fromEnvironment(roundEnv)
                .groupBy { it.enclosingElement }
        val classesToAttrsInfo = AttrInfo.fromEnvironment(roundEnv, elementUtils, typeUtils, resourceScanner)
                .groupBy { it.enclosingElement }
        val classesToStyleableFieldInfo = StyleableFieldInfo.fromEnvironment(roundEnv, resourceScanner)
                .groupBy { it.enclosingElement }
        val styleablesInfo: List<StyleableInfo> = StyleableInfo.fromEnvironment(roundEnv,
                elementUtils, typeUtils, resourceScanner, classesToStyleableFieldInfo, classesToBeforeStyleInfo, classesToAfterStyleInfo, classesToAttrsInfo)

        if (!styleablesInfo.isEmpty()) {
            try {
                if (generateParisClass) {
                    ParisWriter.writeFrom(filer, styleablesInfo)
                }
                StyleAppliersWriter.writeFrom(filer, elementUtils, typeUtils, styleablesInfo)
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
