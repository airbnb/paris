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


class ParisProcessor : SkyProcessor() {

    companion object {
        lateinit var INSTANCE: ParisProcessor
    }

    internal val resourceScanner = AndroidResourceScanner()

    internal lateinit var RFinder: RFinder

    internal var defaultStyleNameFormat: String = ""

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        resourceScanner.init(processingEnv)
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        val types: MutableSet<String> = LinkedHashSet()
        return setOf(Styleable::class.java, Attr::class.java)
                .mapTo(types) { it.canonicalName }
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        INSTANCE = this

        RFinder = RFinder()

        roundEnv.getElementsAnnotatedWith(ParisConfig::class.java)
                .firstOrNull()
                ?.getAnnotation(ParisConfig::class.java)
                ?.let {
                    defaultStyleNameFormat = it.defaultStyleNameFormat
                    RFinder.processConfig(it)
                }

        val classesToBeforeStyleInfo = BeforeStyleInfoExtractor()
                .extract(roundEnv)
                .groupBy { it.enclosingElement }
        val classesToAfterStyleInfo = AfterStyleInfoExtractor()
                .extract(roundEnv)
                .groupBy { it.enclosingElement }

        val styleableChildrenInfo = StyleableChildInfoExtractor().extract(roundEnv)
        val classesToStyleableChildrenInfo = styleableChildrenInfo.groupBy { it.enclosingElement }

        val attrsInfo = AttrInfoExtractor().extract(roundEnv)
        val classesToAttrsInfo = attrsInfo.groupBy { it.enclosingElement }

        RFinder.processResourceAnnotations(styleableChildrenInfo, attrsInfo)

        val classesToStylesInfo = StyleInfoExtractor()
                .fromEnvironment(roundEnv)
                .groupBy { it.enclosingElement }

        val styleablesInfo: List<StyleableInfo> = StyleableInfoExtractor()
                .fromEnvironment(
                        roundEnv,
                        classesToStyleableChildrenInfo,
                        classesToBeforeStyleInfo,
                        classesToAfterStyleInfo,
                        classesToAttrsInfo,
                        classesToStylesInfo
                )

        RFinder.processStyleables(styleablesInfo)

        val externalStyleablesInfo = BaseStyleableInfoExtractor().fromEnvironment()

        if (!styleablesInfo.isEmpty()) {
            try {
                ModuleJavaClass(styleablesInfo).write()

                if (RFinder.element != null) {
                    val parisClassPackageName = RFinder.element!!.packageName
                    ParisJavaClass(parisClassPackageName, styleablesInfo, externalStyleablesInfo).write()
                }

                val styleablesTree = StyleablesTree(styleablesInfo + externalStyleablesInfo)
                for (styleableInfo in styleablesInfo) {
                    StyleApplierJavaClass(styleablesTree, styleableInfo).write()
                }
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
