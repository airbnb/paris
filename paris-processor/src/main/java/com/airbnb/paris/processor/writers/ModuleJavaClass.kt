package com.airbnb.paris.processor.writers

import com.airbnb.paris.annotations.GeneratedStyleableClass
import com.airbnb.paris.annotations.GeneratedStyleableModule
import com.airbnb.paris.processor.MODULE_SIMPLE_CLASS_NAME_FORMAT
import com.airbnb.paris.processor.PARIS_MODULES_PACKAGE_NAME
import com.airbnb.paris.processor.ParisProcessor
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.models.StyleableInfo
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.TypeSpec
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Module classes index the styleable views available in their module. Since they are all put in the
 * same, predefined package, modules are able to retrieve and use styleable classes from their
 * dependencies through these classes
 */
internal class ModuleJavaClass(
    override val processor: ParisProcessor,
    private val styleablesInfo: List<StyleableInfo>
) : SkyJavaClass(processor) {

    override val packageName = PARIS_MODULES_PACKAGE_NAME
    override val name: String

    init {
        // The class name is a hash of all the styleable views' canonical names so the likelihood of
        // a naming conflict is insignificantly small
        val styleablesConcat = styleablesInfo
            .map { it.elementPackageName + it.elementName }
            .reduce { acc, s -> "$acc,$s" }
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.update(styleablesConcat.toByteArray(), 0, styleablesConcat.length)
        val hash = BigInteger(1, messageDigest.digest()).toString(16)

        // The class name has to be prefixed with something since the hash could start with a
        // number, which is not allowed in class names
        name = String.format(MODULE_SIMPLE_CLASS_NAME_FORMAT, hash)
    }

    override val block: TypeSpec.Builder.() -> Unit = {

        annotation(GeneratedStyleableModule::class.java) {
            value {
                add("{")
                for (styleableInfo in styleablesInfo) {
                    add("\$L,", AnnotationSpec.builder(GeneratedStyleableClass::class.java).apply {
                        value("\$T.class", styleableInfo.elementType)
                    }.build())
                }
                add("}")
            }
        }

        public()
        final()

    }
}
