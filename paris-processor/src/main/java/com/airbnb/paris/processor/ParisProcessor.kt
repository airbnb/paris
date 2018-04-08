package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.ParisConfig
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceScanner
import com.airbnb.paris.processor.framework.SkyProcessor
import com.airbnb.paris.processor.framework.errors.Errors
import com.airbnb.paris.processor.framework.errors.ProcessorException
import com.airbnb.paris.processor.framework.packageName
import com.airbnb.paris.processor.models.*
import com.airbnb.paris.processor.writers.ModuleJavaClass
import com.airbnb.paris.processor.writers.ParisJavaClass
import com.airbnb.paris.processor.writers.StyleApplierJavaClass
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement


class ParisProcessor : SkyProcessor() {

    companion object {
        lateinit var INSTANCE: ParisProcessor
    }

    internal val resourceScanner = AndroidResourceScanner()

    internal val rFinder = RFinder()

    internal var defaultStyleNameFormat: String = ""

    private var beforeStyleInfoExtractor = BeforeStyleInfoExtractor()

    private var afterStyleInfoExtractor = AfterStyleInfoExtractor()

    private var styleableChildInfoExtractor = StyleableChildInfoExtractor()

    private var attrInfoExtractor = AttrInfoExtractor()

    private var styleInfoExtractor = StyleInfoExtractor()

    private var styleableInfoExtractor = StyleableInfoExtractor()

    private val externalStyleablesInfo by lazy { BaseStyleableInfoExtractor().fromEnvironment() }

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        resourceScanner.init(processingEnv)
        INSTANCE = this
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        val types: MutableSet<String> = LinkedHashSet()
        return setOf(Styleable::class.java, Attr::class.java)
                .mapTo(types) { it.canonicalName }
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun processRound(roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(ParisConfig::class.java)
                .firstOrNull()
                ?.getAnnotation(ParisConfig::class.java)
                ?.let {
                    defaultStyleNameFormat = it.defaultStyleNameFormat
                    rFinder.processConfig(it)
                }

        beforeStyleInfoExtractor.process(roundEnv)
        val classesToBeforeStyleInfo = beforeStyleInfoExtractor.latest.groupBy { it.enclosingElement }

        afterStyleInfoExtractor.process(roundEnv)
        val classesToAfterStyleInfo = afterStyleInfoExtractor.latest.groupBy { it.enclosingElement }

        styleableChildInfoExtractor.process(roundEnv)
        val styleableChildrenInfo = styleableChildInfoExtractor.latest
        val classesToStyleableChildrenInfo = styleableChildrenInfo.groupBy { it.enclosingElement }

        attrInfoExtractor.process(roundEnv)
        val attrsInfo = attrInfoExtractor.latest
        val classesToAttrsInfo = attrsInfo.groupBy { it.enclosingElement }

        rFinder.processResourceAnnotations(styleableChildrenInfo, attrsInfo)

        styleInfoExtractor.process(roundEnv)
        val classesToStylesInfo = styleInfoExtractor.latest.groupBy { it.enclosingElement }

        val styleablesInfo: List<StyleableInfo> = styleableInfoExtractor.process(
                roundEnv,
                classesToStyleableChildrenInfo,
                classesToBeforeStyleInfo,
                classesToAfterStyleInfo,
                classesToAttrsInfo,
                classesToStylesInfo
        )

        rFinder.processStyleables(styleablesInfo)

        val styleablesTree = StyleablesTree(styleablesInfo + externalStyleablesInfo)
        for (styleableInfo in styleablesInfo) {
            StyleApplierJavaClass(styleablesTree, styleableInfo).write()
        }
    }

    override fun claimAnnotations(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        return false
    }

    override fun processingOver() {
        val styleablesInfo = styleableInfoExtractor.models

        if (!styleablesInfo.isEmpty()) {
            try {
                ModuleJavaClass(styleablesInfo).write()
            } catch (e: ProcessorException) {
                Errors.log(e)
            }
        }

        if (!styleablesInfo.isEmpty() || !externalStyleablesInfo.isEmpty()) {
            try {
                if (rFinder.element != null) {
                    val parisClassPackageName = rFinder.element!!.packageName
                    ParisJavaClass(parisClassPackageName, styleablesInfo, externalStyleablesInfo).write()
                }
            } catch (e: ProcessorException) {
                Errors.log(e)
            }
        }

        Errors.printLoggedErrorsIfAny(messager)
    }
}
