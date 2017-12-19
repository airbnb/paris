package com.airbnb.paris.processor.writers

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.*
import com.airbnb.paris.processor.models.StyleableInfo
import com.squareup.javapoet.*
import java.io.*
import java.math.*
import java.security.*
import javax.lang.model.element.*

/**
 * Module classes index the styleable views available in their module. Since they are all put in the
 * same, predefined package, modules are able to retrieve and use styleable classes from their
 * dependencies through these classes
 */
internal class ModuleWriter(processor: ParisProcessor) : ParisHelper(processor) {

    @Throws(IOException::class)
    internal fun writeFrom(styleablesInfo: List<StyleableInfo>) {
        // The information is stored in annotations which makes it easy to retrieve. This is, after
        // all, an annotation processor
        val classesCode = CodeBlock.builder().add("{")
        for (styleableInfo in styleablesInfo) {
            classesCode.add("\$L,", AnnotationSpec.builder(GeneratedStyleableClass::class.java)
                    .addMember("value", "\$T.class", styleableInfo.elementType)
                    .build())
        }
        classesCode.add("}")

        val styleableModuleAnnotationBuilder = AnnotationSpec.builder(GeneratedStyleableModule::class.java)
                .addMember("value", classesCode.build())

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
        val styleableModuleTypeBuilder = TypeSpec.classBuilder(String.format(MODULE_SIMPLE_CLASS_NAME_FORMAT, hash))
                .addAnnotation(styleableModuleAnnotationBuilder.build())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

        JavaFile.builder(PARIS_MODULES_PACKAGE_NAME, styleableModuleTypeBuilder.build())
                .build()
                .writeTo(filer)
    }
}
