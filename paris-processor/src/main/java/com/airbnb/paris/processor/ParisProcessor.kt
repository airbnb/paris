package com.airbnb.paris.processor

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.android_resource_scanner.*
import com.airbnb.paris.processor.utils.Errors
import com.airbnb.paris.processor.utils.ProcessorException
import com.airbnb.paris.processor.utils.asTypeElement
import com.airbnb.paris.processor.utils.className
import com.squareup.javapoet.ClassName
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.*
import javax.lang.model.element.*
import javax.lang.model.type.*
import javax.lang.model.util.*
import kotlin.check


class ParisProcessor : AbstractProcessor() {

    companion object {
        private val supportedAnnotations: Set<Class<out Annotation>> = setOf(Styleable::class.java, Attr::class.java)
    }

    var defaultStyleNameFormat: String = ""
    var RElement: TypeElement? = null

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

        val config = roundEnv
                .getElementsAnnotatedWith(ParisConfig::class.java)
                .firstOrNull()
                ?.getAnnotation(ParisConfig::class.java)
        if (config != null) {
            defaultStyleNameFormat = config.defaultStyleNameFormat
            generateParisClass = config.generateParisClass

            check(defaultStyleNameFormat.isBlank() || RElement != null) {
                "If defaultStyleNameFormat is specified, rClass must be as well"
            }
        }

        val classesToBeforeStyleInfo = BeforeStyleInfo.fromEnvironment(this, roundEnv)
                .groupBy { it.enclosingElement }
        val classesToAfterStyleInfo = AfterStyleInfo.fromEnvironment(this, roundEnv)
                .groupBy { it.enclosingElement }
        val styleableChildrenInfo = StyleableChildInfo.fromEnvironment(roundEnv, resourceScanner)
        val classesToStyleableChildrenInfo = styleableChildrenInfo.groupBy { it.enclosingElement }
        val attrsInfo = AttrInfo.fromEnvironment(roundEnv, elementUtils, typeUtils, resourceScanner)
        val classesToAttrsInfo = attrsInfo.groupBy { it.enclosingElement }
        val classesToStylesInfo = StyleInfo.fromEnvironment(this, roundEnv)
                .groupBy { it.enclosingElement }
        val styleablesInfo: List<StyleableInfo> = StyleableInfo.fromEnvironment(
                roundEnv, elementUtils, typeUtils,
                classesToStyleableChildrenInfo,
                classesToBeforeStyleInfo,
                classesToAfterStyleInfo,
                classesToAttrsInfo,
                classesToStylesInfo)

        RElement = findRElement(config, styleablesInfo, styleableChildrenInfo, attrsInfo)

        val externalStyleablesInfo = mutableListOf<BaseStyleableInfo>()
        elementUtils.getPackageElement(PARIS_MODULES_PACKAGE_NAME)?.let { packageElement ->
            for (moduleElement in packageElement.enclosedElements) {
                val styleableModule = moduleElement.getAnnotation(GeneratedStyleableModule::class.java)
                externalStyleablesInfo.addAll(
                        styleableModule.value
                                .mapNotNull<GeneratedStyleableClass, TypeElement> {
                                    var typeElement: TypeElement? = null
                                    try {
                                        it.value
                                    } catch (e: MirroredTypeException) {
                                        typeElement = e.typeMirror.asTypeElement(typeUtils)
                                    }
                                    typeElement
                                }
                                .map { BaseStyleableInfo.fromElement(elementUtils, typeUtils, it) }
                )
            }
        }

        if (!styleablesInfo.isEmpty()) {
            try {
                ModuleWriter.writeFrom(filer, styleablesInfo)

                if (generateParisClass) {
                    val parisClassPackageName = elementUtils.getPackageOf(RElement!!).qualifiedName.toString()
                    ParisWriter.writeFrom(filer, parisClassPackageName, styleablesInfo, externalStyleablesInfo)
                }

                StyleAppliersWriter.writeFrom(filer, elementUtils, typeUtils, styleablesInfo, externalStyleablesInfo)
            } catch (e: ProcessorException) {
                Errors.log(e)
            }
        }

        if (roundEnv.processingOver()) {
            Errors.printLoggedErrorsIfAny(messager)
        }

        return true
    }

    private fun findRElement(
            config: ParisConfig?,
            styleablesInfo: List<StyleableInfo>,
            styleableChildrenInfo: List<StyleableChildInfo>,
            attrsInfo: List<AttrInfo>
    ): TypeElement? {

        // First try to get it from the config
        var rType: TypeMirror?
        config?.let {
            rType = getRTypeFromConfig(config)

            // TODO Move check to getRTypeFromConfig
            check(rType == null || rType!!.asTypeElement(typeUtils).simpleName.toString() == "R") {
                "@ParisConfig's rClass parameter is pointing to a non-R class"
            }

            rType?.let {
                return it.asTypeElement(typeUtils)
            }
        }

        // Second try to get it from @Attr values
        val arbitraryResId = when {
            styleableChildrenInfo.isNotEmpty() -> styleableChildrenInfo[0].styleableResId
            attrsInfo.isNotEmpty() -> attrsInfo[0].styleableResId
            else -> null
        }
        arbitraryResId?.let {
            return elementUtils.getTypeElement(it.className!!.enclosingClassName().reflectionName())
        }

        // Third try to find R based on the package names of an arbitrary @Styleable class
        if (styleablesInfo.isNotEmpty()) {
            val arbitraryStyleableInfo = styleablesInfo[0]

            var packageName = arbitraryStyleableInfo.elementPackageName
            while (packageName.isNotBlank()) {
                elementUtils.getTypeElement("$packageName.R")?.let {
                    return it
                }
                val lastIndexOfDot = packageName.lastIndexOf('.')
                packageName = if (lastIndexOfDot > 0) {
                    packageName.substring(0, lastIndexOfDot)
                } else {
                    ""
                }
            }
        }

        return null
    }

    private fun getRTypeFromConfig(config: ParisConfig): TypeMirror? {
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
