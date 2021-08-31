package com.airbnb.paris.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XRoundEnv
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.ParisConfig
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.android_resource_scanner.JavacResourceScanner
import com.airbnb.paris.processor.android_resource_scanner.KspResourceScanner
import com.airbnb.paris.processor.android_resource_scanner.ResourceScanner
import com.airbnb.paris.processor.framework.Memoizer
import com.airbnb.paris.processor.framework.Message
import com.airbnb.paris.processor.models.AfterStyleInfoExtractor
import com.airbnb.paris.processor.models.AttrInfoExtractor
import com.airbnb.paris.processor.models.BaseStyleableInfo
import com.airbnb.paris.processor.models.BaseStyleableInfoExtractor
import com.airbnb.paris.processor.models.BeforeStyleInfoExtractor
import com.airbnb.paris.processor.models.StyleInfoExtractor
import com.airbnb.paris.processor.models.StyleableChildInfoExtractor
import com.airbnb.paris.processor.models.StyleableInfo
import com.airbnb.paris.processor.models.StyleableInfoExtractor
import com.airbnb.paris.processor.utils.enclosingElementIfApplicable
import com.airbnb.paris.processor.writers.ModuleJavaClass
import com.airbnb.paris.processor.writers.ParisJavaClass
import com.airbnb.paris.processor.writers.StyleApplierJavaClass
import com.airbnb.paris.processor.writers.StyleExtensionsKotlinFile
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.SourceVersion
import javax.tools.Diagnostic
import kotlin.reflect.KClass

@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class ParisProcessor(
    kspEnvironment: SymbolProcessorEnvironment? = null
) : BaseProcessor(kspEnvironment) {

    val loggedMessages: MutableList<Message> = mutableListOf()

    lateinit var resourceScanner: ResourceScanner

    internal val rFinder = RFinder(this)
    val RElement: XTypeElement? get() = rFinder.element

    var defaultStyleNameFormat: String = ""

    var namespacedResourcesEnabled: Boolean = false

    val memoizer = Memoizer(this)

    private val beforeStyleInfoExtractor = BeforeStyleInfoExtractor(this)

    private val afterStyleInfoExtractor = AfterStyleInfoExtractor(this)

    private val styleableChildInfoExtractor = StyleableChildInfoExtractor(this)

    private val attrInfoExtractor = AttrInfoExtractor(this)

    private val styleInfoExtractor = StyleInfoExtractor(this)

    private val styleableInfoExtractor = StyleableInfoExtractor(this)

    private lateinit var externalStyleablesInfo: List<BaseStyleableInfo>

    init {
        if (kspEnvironment != null) {
            resourceScanner = KspResourceScanner()
        }
    }

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        resourceScanner = JavacResourceScanner(processingEnv)
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(
            Attr::class.java,
            ParisConfig::class.java,
            Styleable::class.java
        ).map { it.canonicalName }.toSet()
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    var roundCount = 0
    override fun process(environment: XProcessingEnv, round: XRoundEnv) {
        // Writing to the paris and module class file on every round causes an infinite loop, because it triggers another round.
        // We could write to that file only in finish once we collect all styleables, or just force a single round here (which
        // assumes that no other code generates styleables, which we've never supported anyway).
        if (roundCount > 0) return
        println("processing round $roundCount")
        roundCount++

        round.getElementsAnnotatedWith(ParisConfig::class)
            .firstOrNull()
            ?.getAnnotation(ParisConfig::class)
            ?.let {
                defaultStyleNameFormat = it.value.defaultStyleNameFormat
                namespacedResourcesEnabled = it.value.namespacedResourcesEnabled
                rFinder.processConfig(it)
            }

        beforeStyleInfoExtractor.process(round)
        val classesToBeforeStyleInfo =
            beforeStyleInfoExtractor.latest.groupBy { it.enclosingElement }

        afterStyleInfoExtractor.process(round)
        val classesToAfterStyleInfo = afterStyleInfoExtractor.latest.groupBy { it.enclosingElement }

        styleableChildInfoExtractor.process(round)
        val styleableChildrenInfo = styleableChildInfoExtractor.latest
        val classesToStyleableChildrenInfo = styleableChildrenInfo.groupBy { it.enclosingElement }

        attrInfoExtractor.process(round)
        val attrsInfo = attrInfoExtractor.latest
        val classesToAttrsInfo = attrsInfo.groupBy { it.enclosingElement }

        rFinder.processResourceAnnotations(styleableChildrenInfo, attrsInfo)

        styleInfoExtractor.process(round)
        val classesToStylesInfo = styleInfoExtractor.latest.groupBy { it.enclosingElement }

        val styleablesInfo: List<StyleableInfo> = styleableInfoExtractor.process(
            roundEnv = round,
            classesToStyleableChildInfo = classesToStyleableChildrenInfo,
            classesToBeforeStyleInfo = classesToBeforeStyleInfo,
            classesToAfterStyleInfo = classesToAfterStyleInfo,
            classesToAttrsInfo = classesToAttrsInfo,
            classesToStylesInfo = classesToStylesInfo
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

    override fun onError() {
        printLogsIfAny()
    }

    override fun finish() {
        // Errors and warnings are only printed at the end to generate as many classes as possible
        // and avoid "could not find" errors which make debugging harder
        printLogsIfAny()
    }

    fun printLogsIfAny() {
        loggedMessages.forEach { message ->
            val kind = when (message.severity) {
                Message.Severity.Warning -> Diagnostic.Kind.WARNING
                Message.Severity.Error -> Diagnostic.Kind.ERROR
            }
            val element = message.element
            environment.messager.printMessage(kind, message.message + " [$element : ${element?.enclosingElementIfApplicable}]", element)
        }
        loggedMessages.clear()
    }

    fun getResourceId(annotation: KClass<out Annotation>, element: XElement, value: Int): AndroidResourceId? {
        val resourceId = resourceScanner.getId(annotation, element, value)
        if (resourceId == null) {
            logError(element) {
                "Could not retrieve Android resource ID from annotation."
            }
        }
        return resourceId
    }

    fun logError(element: XElement? = null, lazyMessage: () -> String) {
        log(Message.Severity.Error, element, lazyMessage)
    }

    fun logWarning(element: XElement? = null, lazyMessage: () -> String) {
        log(Message.Severity.Warning, element, lazyMessage)
    }

    fun log(severity: Message.Severity, element: XElement? = null, lazyMessage: () -> String) {
        loggedMessages.add(Message(severity, lazyMessage(), element))
    }
}
