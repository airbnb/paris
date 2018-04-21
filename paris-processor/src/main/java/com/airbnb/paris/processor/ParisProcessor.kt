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
import com.airbnb.paris.processor.writers.StyleExtensionsKotlinFile
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

    // TODO Don't do this lazily to make sure it happens before we create new generated module classes
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

        val allStyleables = styleablesInfo + externalStyleablesInfo
        val styleablesTree = StyleablesTree(allStyleables)
        for (styleableInfo in styleablesInfo) {
            StyleApplierJavaClass(styleablesTree, styleableInfo).write()
            StyleExtensionsKotlinFile(styleableInfo).write()
        }

        if (styleablesInfo.isNotEmpty()) {
            try {
                ModuleJavaClass(styleablesInfo).write()
            } catch (e: ProcessorException) {
                Errors.log(e)
            }
        }

        if (allStyleables.isNotEmpty()) {
            try {

                checkNotNull(rFinder.element) {
                    "Unable to locate R class. Please annotate an arbitrary package with @ParisConfig and set the rClass parameter to the R class."
                }

                val parisClassPackageName = rFinder.element!!.packageName
                ParisJavaClass(parisClassPackageName, styleablesInfo, externalStyleablesInfo).write()

            } catch (e: Exception) {
                Errors.log(e)
            }
        }
    }

    override fun claimAnnotations(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // Let other annotation processors use them if they want
        return false
    }

    override fun processingOver() {
        Errors.printLoggedErrorsIfAny(messager)
    }
}
