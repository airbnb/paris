package com.airbnb.paris.processor.android_resource_scanner

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.compat.XConverters.toJavac
import com.squareup.javapoet.ClassName
import com.sun.source.util.Trees
import com.sun.tools.javac.code.Symbol.VarSymbol
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.JCTree.JCFieldAccess
import com.sun.tools.javac.tree.TreeScanner
import java.util.HashMap
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.reflect.KClass

class JavacResourceScanner(
    processingEnv: ProcessingEnvironment
) : ResourceScanner {
    private val typeUtils: Types = processingEnv.typeUtils
    private val elementUtils: Elements = processingEnv.elementUtils
    private var trees: Trees? = null

    init {
        trees = try {
            Trees.instance(processingEnv)
        } catch (ignored: IllegalArgumentException) {
            try {
                // Get original ProcessingEnvironment from Gradle-wrapped one or KAPT-wrapped one.
                // In Kapt, its field is called "delegate". In Gradle's, it's called "processingEnv"
                processingEnv.javaClass.declaredFields.mapNotNull { field ->
                    if (field.name == "delegate" || field.name == "processingEnv") {
                        field.isAccessible = true
                        val javacEnv = field[processingEnv] as ProcessingEnvironment
                        Trees.instance(javacEnv)
                    } else {
                        null
                    }
                }.firstOrNull()
            } catch (ignored2: Throwable) {
                null
            }
        }
    }

    /**
     * Returns the [AndroidResourceId] that is used as an annotation value of the given [XElement]
     */
    override fun getId(
        annotation: KClass<out Annotation>,
        element: XElement,
        value: Int
    ): AndroidResourceId? {
        val results = getResults(annotation.java, element.toJavac())
        return if (results.containsKey(value)) {
            results[value]
        } else {
            null
        }
    }

    private fun getResults(
        annotation: Class<out Annotation?>,
        element: Element
    ): Map<Int, AndroidResourceId> {
        val scanner = AnnotationScanner()
        // TODO I suspect the annotation mirror isn't needed here because StyleableChildInfo was using the wrong annotation class for a long time
        val tree = trees?.getTree(
            element,
            getMirror(element, annotation)
        ) as JCTree?
        tree?.accept(scanner)
        return scanner.results()
    }

    private inner class AnnotationScanner : TreeScanner() {
        private val results: MutableMap<Int, AndroidResourceId> = HashMap()

        override fun visitSelect(jcFieldAccess: JCFieldAccess) {
            val symbol = jcFieldAccess.sym
            if ((symbol as? VarSymbol)?.enclosingElement?.enclosingElement?.enclClass() != null) {
                parseResourceSymbol(symbol)
            }
        }

        private fun parseResourceSymbol(symbol: VarSymbol) {
            // eg com.airbnb.paris.R
            val rClass = symbol.enclosingElement.enclosingElement.enclClass().className()
            // eg styleable
            val rTypeClass = symbol.enclosingElement.simpleName.toString()
            // eg View_background
            val resourceName = symbol.simpleName.toString()
            val value = symbol.constantValue as? Int ?: return
            val androidResourceId = AndroidResourceId(value, getClassName(rClass, rTypeClass), resourceName)
            results[androidResourceId.value] = androidResourceId
        }

        fun results(): Map<Int, AndroidResourceId> = results
    }

    private fun getClassName(rClass: String, rTypeClass: String): ClassName {
        val rClassElement: Element?
        rClassElement = try {
            elementUtils.getTypeElement(rClass)
        } catch (mte: MirroredTypeException) {
            typeUtils.asElement(mte.typeMirror)
        }
        val rClassPackageName = elementUtils.getPackageOf(rClassElement).qualifiedName.toString()
        return ClassName.get(rClassPackageName, "R", rTypeClass)
    }

    companion object {
        private fun getMirror(
            element: Element,
            annotation: Class<out Annotation?>
        ): AnnotationMirror? {
            val targetName = annotation.canonicalName
            return element.annotationMirrors.firstOrNull { it.annotationType.toString() == targetName }
        }
    }
}