package com.airbnb.paris.processor

import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XRoundEnv
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.ParisConfig
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceScanner
import com.airbnb.paris.processor.framework.Memoizer
import com.airbnb.paris.processor.framework.JavaSkyProcessor
import com.airbnb.paris.processor.framework.packageName
import com.airbnb.paris.processor.models.AfterStyleInfoExtractor
import com.airbnb.paris.processor.models.AttrInfoExtractor
import com.airbnb.paris.processor.models.BaseStyleableInfo
import com.airbnb.paris.processor.models.BaseStyleableInfoExtractor
import com.airbnb.paris.processor.models.BeforeStyleInfoExtractor
import com.airbnb.paris.processor.models.StyleInfoExtractor
import com.airbnb.paris.processor.models.StyleableChildInfoExtractor
import com.airbnb.paris.processor.models.StyleableInfo
import com.airbnb.paris.processor.models.StyleableInfoExtractor
import com.airbnb.paris.processor.writers.ModuleJavaClass
import com.airbnb.paris.processor.writers.ParisJavaClass
import com.airbnb.paris.processor.writers.StyleApplierJavaClass
import com.airbnb.paris.processor.writers.StyleExtensionsKotlinFile
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class ParisProcessor : JavaSkyProcessor(), WithParisProcessor {

    override val processor = this

    internal val resourceScanner = AndroidResourceScanner()

    internal val rFinder = RFinder(this)

    override var defaultStyleNameFormat: String = ""

    override var namespacedResourcesEnabled: Boolean = false

    override val memoizer = Memoizer(this)

    private val beforeStyleInfoExtractor = BeforeStyleInfoExtractor(this)

    private val afterStyleInfoExtractor = AfterStyleInfoExtractor(this)

    private val styleableChildInfoExtractor = StyleableChildInfoExtractor(this)

    private val attrInfoExtractor = AttrInfoExtractor(this)

    private val styleInfoExtractor = StyleInfoExtractor(this)

    private val styleableInfoExtractor = StyleableInfoExtractor(this)

    private lateinit var externalStyleablesInfo: List<BaseStyleableInfo>

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        resourceScanner.init(processingEnv)
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

        val xProcessingEnv = XProcessingEnv.create(processingEnv)
        val xRoundEnv = XRoundEnv.create(xProcessingEnv, roundEnv)

        // TODO: 2/22/21 Package annotation support in ksp?
        roundEnv.getElementsAnnotatedWith(ParisConfig::class.java)
            .firstOrNull()
            ?.getAnnotation(ParisConfig::class.java)
            ?.let {
                defaultStyleNameFormat = it.defaultStyleNameFormat
                namespacedResourcesEnabled = it.namespacedResourcesEnabled
                rFinder.processConfig(it)
            }

        beforeStyleInfoExtractor.process(xRoundEnv)
        val classesToBeforeStyleInfo =
            beforeStyleInfoExtractor.latest.groupBy { it.enclosingElement }

        afterStyleInfoExtractor.process(xRoundEnv)
        val classesToAfterStyleInfo = afterStyleInfoExtractor.latest.groupBy { it.enclosingElement }

        styleableChildInfoExtractor.process(xRoundEnv)
        val styleableChildrenInfo = styleableChildInfoExtractor.latest
        val classesToStyleableChildrenInfo = styleableChildrenInfo.groupBy { it.enclosingElement }

        attrInfoExtractor.process(xRoundEnv)
        val attrsInfo = attrInfoExtractor.latest
        val classesToAttrsInfo = attrsInfo.groupBy { it.enclosingElement }

        rFinder.processResourceAnnotations(styleableChildrenInfo, attrsInfo)

        styleInfoExtractor.process(xRoundEnv)
        val classesToStylesInfo = styleInfoExtractor.latest.groupBy { it.enclosingElement }

        val styleablesInfo: List<StyleableInfo> = styleableInfoExtractor.process(
            xRoundEnv,
            classesToStyleableChildrenInfo,
            classesToBeforeStyleInfo,
            classesToAfterStyleInfo,
            classesToAttrsInfo,
            classesToStylesInfo
        )

        rFinder.processStyleables(styleablesInfo)

        /** Make sure to get these before writing the [ModuleJavaClass] for this module */
        externalStyleablesInfo = BaseStyleableInfoExtractor(this).fromEnvironment()

        val allStyleables = styleablesInfo + externalStyleablesInfo
        if (allStyleables.isNotEmpty() && rFinder.element == null) {
            logError {
                "Unable to locate R class. Please annotate an arbitrary package with @ParisConfig and set the rClass parameter to the R class."
            }
            return
        }
        val styleablesTree = StyleablesTree(this, allStyleables)
        for (styleableInfo in styleablesInfo) {
            StyleApplierJavaClass(this, styleablesTree, styleableInfo).write()
            StyleExtensionsKotlinFile(this, styleableInfo).write()
        }

        if (styleablesInfo.isNotEmpty()) {
            ModuleJavaClass(this, styleablesInfo).write()
        }

        if (allStyleables.isNotEmpty()) {
            val parisClassPackageName = rFinder.element!!.packageName
            ParisJavaClass(
                this,
                parisClassPackageName,
                styleablesInfo,
                externalStyleablesInfo
            ).write()
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
        printLogsIfAny()
    }
}
