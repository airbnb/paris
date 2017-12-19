package com.airbnb.paris.processor

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.android_resource_scanner.*
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.models.*
import com.airbnb.paris.processor.utils.*
import com.airbnb.paris.processor.writers.*
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.*
import javax.lang.model.element.*
import javax.lang.model.type.*


class ParisProcessor : SkyProcessor() {

    companion object {
        private val supportedAnnotations: Set<Class<out Annotation>> = setOf(Styleable::class.java, Attr::class.java)
    }

    var defaultStyleNameFormat: String = ""

    internal val resourceScanner = AndroidResourceScanner()

    internal lateinit var RFinder: RFinder

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        resourceScanner.init(processingEnv)
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        val types: MutableSet<String> = LinkedHashSet()
        supportedAnnotations.mapTo(types) { it.canonicalName }
        return types
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        var generateParisClass = true
        RFinder = RFinder(this)

        val config = roundEnv
                .getElementsAnnotatedWith(ParisConfig::class.java)
                .firstOrNull()
                ?.getAnnotation(ParisConfig::class.java)
        if (config != null) {
            defaultStyleNameFormat = config.defaultStyleNameFormat
            generateParisClass = config.generateParisClass
            RFinder.processConfig(config)
        }

        val classesToBeforeStyleInfo = BeforeStyleInfoExtractor(this)
                .fromEnvironment(roundEnv)
                .groupBy { it.enclosingElement }
        val classesToAfterStyleInfo = AfterStyleInfoExtractor(this)
                .fromEnvironment(roundEnv)
                .groupBy { it.enclosingElement }

        val styleableChildrenInfo = StyleableChildInfoExtractor(this).fromEnvironment(roundEnv)
        val classesToStyleableChildrenInfo = styleableChildrenInfo.groupBy { it.enclosingElement }

        val attrsInfo = AttrInfoExtractor(this).fromEnvironment(roundEnv)
        val classesToAttrsInfo = attrsInfo.groupBy { it.enclosingElement }

        RFinder.processResourceAnnotations(styleableChildrenInfo, attrsInfo)

        val classesToStylesInfo = StyleInfoExtractor(this)
                .fromEnvironment(roundEnv)
                .groupBy { it.enclosingElement }

        val styleablesInfo: List<StyleableInfo> = StyleableInfoExtractor(this)
                .fromEnvironment(
                        roundEnv,
                        classesToStyleableChildrenInfo,
                        classesToBeforeStyleInfo,
                        classesToAfterStyleInfo,
                        classesToAttrsInfo,
                        classesToStylesInfo
                )

        RFinder.processStyleables(styleablesInfo)

        val externalStyleablesInfo = mutableListOf<BaseStyleableInfo>()
        elements.getPackageElement(PARIS_MODULES_PACKAGE_NAME)?.let { packageElement ->
            for (moduleElement in packageElement.enclosedElements) {
                val styleableModule = moduleElement.getAnnotation(GeneratedStyleableModule::class.java)
                externalStyleablesInfo.addAll(
                        styleableModule.value
                                .mapNotNull<GeneratedStyleableClass, TypeElement> {
                                    var typeElement: TypeElement? = null
                                    try {
                                        it.value
                                    } catch (e: MirroredTypeException) {
                                        typeElement = e.typeMirror.asTypeElement(types)
                                    }
                                    typeElement
                                }
                                .map { BaseStyleableInfoExtractor(this).fromElement(it) }
                )
            }
        }

        if (!styleablesInfo.isEmpty()) {
            try {
                ModuleWriter(this).writeFrom(styleablesInfo)

                if (generateParisClass) {
                    val parisClassPackageName = RFinder.element!!.packageName
                    ParisWriter(this).writeFrom(parisClassPackageName, styleablesInfo, externalStyleablesInfo)
                }

                StyleAppliersWriter(this).writeFrom(styleablesInfo, externalStyleablesInfo)
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
