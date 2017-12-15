package com.airbnb.paris.processor

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.android_resource_scanner.*
import com.airbnb.paris.processor.utils.Errors
import com.airbnb.paris.processor.utils.ProcessorException
import com.airbnb.paris.processor.utils.asTypeElement
import com.airbnb.paris.processor.utils.className
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.*
import javax.lang.model.element.*
import javax.lang.model.type.*
import javax.lang.model.util.*
import kotlin.check


class ParisProcessor : AbstractProcessor() {

    companion object {
        internal const val PARIS_PACKAGE_NAME = "com.airbnb.paris"
        internal const val STYLE_APPLIER_CLASS_NAME_FORMAT = "%sStyleApplier"

        internal val PARIS_CLASS_NAME = "$PARIS_PACKAGE_NAME.Paris".className()
        internal val STYLE_CLASS_NAME = "$PARIS_PACKAGE_NAME.styles.Style".className()
        internal val STYLE_APPLIER_CLASS_NAME = "$PARIS_PACKAGE_NAME.StyleApplier".className()
        internal val STYLE_BUILDER_CLASS_NAME = "$PARIS_PACKAGE_NAME.StyleBuilder".className()
        internal val STYLE_APPLIER_UTILS_CLASS_NAME = "$PARIS_PACKAGE_NAME.StyleApplierUtils".className()
        internal val TYPED_ARRAY_WRAPPER_CLASS_NAME = "$PARIS_PACKAGE_NAME.typed_array_wrappers.TypedArrayWrapper".className()
        internal val STYLE_BUILDER_FUNCTION_CLASS_NAME = "$PARIS_PACKAGE_NAME.utils.StyleBuilderFunction".className()
        internal val RESOURCES_EXTENSIONS_CLASS_NAME = "$PARIS_PACKAGE_NAME.utils.ResourcesExtensionsKt".className()

        internal val BUILT_IN_STYLE_APPLIERS = mapOf(
                Pair("com.airbnb.paris.proxies.ViewProxyStyleApplier", "android.view.View"),
                Pair("com.airbnb.paris.proxies.TextViewProxyStyleApplier", "android.widget.TextView"),
                Pair("com.airbnb.paris.proxies.ImageViewProxyStyleApplier", "android.widget.ImageView"),
                Pair("com.airbnb.paris.proxies.ViewGroupProxyStyleApplier", "android.view.ViewGroup")
        )

        private val supportedAnnotations: Set<Class<out Annotation>> = setOf(Styleable::class.java, Attr::class.java)
    }

    var defaultStyleNameFormat: String = ""
    var rType: TypeMirror? = null

    private val resourceScanner = AndroidResourceScanner()

    lateinit var filer: Filer
    lateinit var messager: Messager
    lateinit var elementUtils: Elements
    lateinit var typeUtils: Types

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

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        var generateParisClass = true

        val configElement = roundEnv.getElementsAnnotatedWith(ParisConfig::class.java).firstOrNull()
        if (configElement != null) {
            val config = configElement.getAnnotation(ParisConfig::class.java)
            defaultStyleNameFormat = config.defaultStyleNameFormat
            rType = getRType(config)
            generateParisClass = config.generateParisClass

            check(defaultStyleNameFormat.isBlank() || rType != null) {
                "If defaultStyleNameFormat is specified, rClass must be as well"
            }
            check(rType == null || rType!!.asTypeElement(typeUtils).simpleName.toString() == "R") {
                "@ParisConfig's rClass parameter is pointing to a non-R class"
            }
        }

        val classesToBeforeStyleInfo = BeforeStyleInfo.fromEnvironment(this, roundEnv)
                .groupBy { it.enclosingElement }
        val classesToAfterStyleInfo = AfterStyleInfo.fromEnvironment(this, roundEnv)
                .groupBy { it.enclosingElement }
        val classesToAttrsInfo = AttrInfo.fromEnvironment(roundEnv, elementUtils, typeUtils, resourceScanner)
                .groupBy { it.enclosingElement }
        val classesToStyleableFieldInfo = StyleableFieldInfo.fromEnvironment(roundEnv, resourceScanner)
                .groupBy { it.enclosingElement }
        val classesToStylesInfo = StyleInfo.fromEnvironment(this, roundEnv)
                .groupBy { it.enclosingElement }
        val styleablesInfo: List<StyleableInfo> = StyleableInfo.fromEnvironment(
                roundEnv, elementUtils, typeUtils,
                classesToStyleableFieldInfo,
                classesToBeforeStyleInfo,
                classesToAfterStyleInfo,
                classesToAttrsInfo,
                classesToStylesInfo)

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

    private fun getRType(config: ParisConfig): TypeMirror? {
        var rType: TypeMirror? = null
        try {
            config.rClass
        } catch (mte: MirroredTypeException) {
            rType = mte.typeMirror
        }

        val voidType = elementUtils.getTypeElement(Void::class.java.canonicalName).asType()
        return if (typeUtils.isSameType(voidType, rType)) {
            null
        } else {
            rType
        }
    }
}
