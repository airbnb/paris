package com.airbnb.paris.processor.writers

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.*
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.models.*
import com.squareup.javapoet.*
import java.math.*
import java.security.*

/**
 * Module classes index the styleable views available in their module. Since they are all put in the
 * same, predefined package, modules are able to retrieve and use styleable classes from their
 * dependencies through these classes
 */
internal class ModuleJavaClass(private val styleablesInfo: List<StyleableInfo>)
    : SkyJavaClass(PARIS_MODULES_PACKAGE_NAME, block = {

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

}) {

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
}
