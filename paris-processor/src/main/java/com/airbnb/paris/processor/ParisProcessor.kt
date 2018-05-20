package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.ParisConfig
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceScanner
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.models.*
import com.airbnb.paris.processor.writers.ModuleJavaClass
import com.airbnb.paris.processor.writers.ParisJavaClass
import com.airbnb.paris.processor.writers.StyleApplierJavaClass
import com.airbnb.paris.processor.writers.StyleExtensionsKotlinFile
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

    private lateinit var externalStyleablesInfo: List<BaseStyleableInfo>

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        resourceScanner.init(processingEnv)
        INSTANCE = this
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(
            Attr::class.java,
            ParisConfig::class.java,
            Styleable::class.java
        ).map { it.canonicalName }.toSet()
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun processRound(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
        // The expectation is that all files will be generated during the first round where we get
        // all the annotated elements. Then a second empty round will happen to give us a chance to
        // do more but we ignore it
        if (annotations.isEmpty()) {
            return
        }

        roundEnv.getElementsAnnotatedWith(ParisConfig::class.java)
            .firstOrNull()
            ?.getAnnotation(ParisConfig::class.java)
            ?.let {
                defaultStyleNameFormat = it.defaultStyleNameFormat
                rFinder.processConfig(it)
            }

        beforeStyleInfoExtractor.process(roundEnv)
        val classesToBeforeStyleInfo =
            beforeStyleInfoExtractor.latest.groupBy { it.enclosingElement }

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

        /** Make sure to get these before writing the [ModuleJavaClass] for this module */
        externalStyleablesInfo = BaseStyleableInfoExtractor().fromEnvironment()

        val allStyleables = styleablesInfo + externalStyleablesInfo
        val styleablesTree = StyleablesTree(allStyleables)
        for (styleableInfo in styleablesInfo) {
            StyleApplierJavaClass(styleablesTree, styleableInfo).write()
            StyleExtensionsKotlinFile(RElement?.className?.toKPoet(), styleableInfo).write()
        }

        if (styleablesInfo.isNotEmpty()) {
            ModuleJavaClass(styleablesInfo).write()
        }

        if (allStyleables.isNotEmpty()) {
            if (rFinder.element == null) {
                logError {
                    "Unable to locate R class. Please annotate an arbitrary package with @ParisConfig and set the rClass parameter to the R class."
                }
            } else {
                val parisClassPackageName = rFinder.element!!.packageName
                ParisJavaClass(
                    parisClassPackageName,
                    styleablesInfo,
                    externalStyleablesInfo
                ).write()
            }
        }
    }

    override fun claimAnnotations(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        // Let other annotation processors use them if they want
        return false
    }

    override fun processingOver() {
        // Errors and warnings are only printed at the end to generate as many classes as possible
        // and avoid "could not find" errors which make debugging harder
        printLogsIfAny(messager)
    }
}
