package com.airbnb.paris.processor

import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XRoundEnv
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.ParisConfig
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceScanner
import com.airbnb.paris.processor.framework.SkyProcessor
import com.airbnb.paris.processor.framework.Memoizer
import com.airbnb.paris.processor.models.AfterStyleInfoExtractor
import com.airbnb.paris.processor.models.AttrInfoExtractor
import com.airbnb.paris.processor.models.BaseStyleableInfo
import com.airbnb.paris.processor.models.BaseStyleableInfoExtractor
import com.airbnb.paris.processor.models.BeforeStyleInfoExtractor
import com.airbnb.paris.processor.models.StyleInfoExtractor
import com.airbnb.paris.processor.models.StyleableChildInfo
import com.airbnb.paris.processor.models.StyleableChildInfoExtractor
import com.airbnb.paris.processor.models.StyleableInfo
import com.airbnb.paris.processor.models.StyleableInfoExtractor
import com.airbnb.paris.processor.writers.ModuleJavaClass
import com.airbnb.paris.processor.writers.ParisJavaClass
import com.airbnb.paris.processor.writers.StyleApplierJavaClass
import com.airbnb.paris.processor.writers.StyleExtensionsKotlinFile
import com.squareup.kotlinpoet.FileSpec
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class ParisProcessor : SkyProcessor(), WithParisProcessor {

    override val processor = this

    internal val rFinder = RFinder(this)

    internal val resourceScanner = AndroidResourceScanner(rFinder, this)

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

        xRoundEnv.getElementsAnnotatedWith(ParisConfig::class)
            .firstOrNull()
            ?.getAnnotation(ParisConfig::class)
            ?.let {
                defaultStyleNameFormat = it.value.defaultStyleNameFormat
                namespacedResourcesEnabled = it.value.namespacedResourcesEnabled
                rFinder.processConfig(it)
            } ?: error("Module must provide a single @ParisConfig annotation to define the R class of this module.")

        val r = rFinder.requireR
        xProcessingEnv.filer.write(FileSpec.builder(r.packageName, "EliRCopy").apply {
            resourceScanner.allResources.forEach { (value, element) ->
                addComment("${element.enclosingElement.className} : $element : $value\n")
            }
        }.build())

        beforeStyleInfoExtractor.process(xRoundEnv)
        val classesToBeforeStyleInfo =
            beforeStyleInfoExtractor.latest.groupBy { it.enclosingElement }

        afterStyleInfoExtractor.process(xRoundEnv)
        val classesToAfterStyleInfo = afterStyleInfoExtractor.latest.groupBy { it.enclosingElement }

        styleableChildInfoExtractor.process(xRoundEnv)
        val styleableChildrenInfo = styleableChildInfoExtractor.latest
        val classesToStyleableChildrenInfo: Map<XTypeElement, List<StyleableChildInfo>> = styleableChildrenInfo.groupBy { it.enclosingElement }
        println(classesToStyleableChildrenInfo.toString())

        attrInfoExtractor.process(xRoundEnv)
        val attrsInfo = attrInfoExtractor.latest
        val classesToAttrsInfo = attrsInfo.groupBy { it.enclosingElement }

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

        /** Make sure to get these before writing the [ModuleJavaClass] for this module */
        externalStyleablesInfo = BaseStyleableInfoExtractor(this).fromEnvironment()

        val allStyleables = styleablesInfo + externalStyleablesInfo
        val styleablesTree = StyleablesTree(this, allStyleables)
        for (styleableInfo in styleablesInfo) {
            StyleApplierJavaClass(this, styleablesTree, styleableInfo).write()
            StyleExtensionsKotlinFile(this, styleableInfo).write()
        }

        if (styleablesInfo.isNotEmpty()) {
            ModuleJavaClass(this, styleablesInfo).write()
        }

        if (allStyleables.isNotEmpty()) {
            val parisClassPackageName = rFinder.requireR.packageName
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
