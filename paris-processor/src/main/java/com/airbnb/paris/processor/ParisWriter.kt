package com.airbnb.paris.processor

import com.airbnb.paris.processor.utils.ClassNames
import com.airbnb.paris.processor.utils.className
import com.squareup.javapoet.*
import java.io.IOException
import java.util.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

internal object ParisWriter {

    @Throws(IOException::class)
    internal fun writeFrom(filer: Filer, styleableClassesInfo: List<StyleableInfo>) {
        val parisTypeBuilder = TypeSpec.classBuilder(ParisProcessor.PARIS_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

        ParisProcessor.BUILT_IN_STYLE_APPLIERS.forEach { styleApplierQualifiedName, viewQualifiedName ->
            val styleApplierClassName = styleApplierQualifiedName.className()
            parisTypeBuilder.addMethod(buildStyleMethod(
                    styleApplierClassName.packageName(),
                    styleApplierClassName.simpleName(),
                    viewQualifiedName.className()))
            parisTypeBuilder.addMethod(buildStyleBuilderMethod(
                    styleApplierClassName.packageName(),
                    styleApplierClassName.simpleName(),
                    viewQualifiedName.className()))
        }

        for (styleableClassInfo in styleableClassesInfo) {
            parisTypeBuilder.addMethod(buildStyleMethod(styleableClassInfo))
            parisTypeBuilder.addMethod(buildStyleBuilderMethod(styleableClassInfo))
        }

        parisTypeBuilder.addMethod(buildAssertStylesMethod(styleableClassesInfo))

        JavaFile.builder(ParisProcessor.PARIS_CLASS_NAME.packageName(), parisTypeBuilder.build())
                .build()
                .writeTo(filer)
    }

    private fun buildStyleMethod(styleableClassInfo: StyleableInfo): MethodSpec {
        return buildStyleMethod(
                styleableClassInfo.elementPackageName,
                String.format(Locale.US, ParisProcessor.STYLE_APPLIER_CLASS_NAME_FORMAT, styleableClassInfo.elementName),
                TypeName.get(styleableClassInfo.viewElementType))
    }

    private fun buildStyleMethod(styleApplierPackageName: String, styleApplierSimpleName: String, viewParameterTypeName: TypeName): MethodSpec {
        val styleApplierClassName = ClassName.get(
                styleApplierPackageName,
                styleApplierSimpleName)
        return MethodSpec.methodBuilder("style")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(styleApplierClassName)
                .addParameter(ParameterSpec.builder(viewParameterTypeName, "view").build())
                .addStatement("return new \$T(view)", styleApplierClassName)
                .build()
    }

    private fun buildStyleBuilderMethod(styleableClassInfo: StyleableInfo): MethodSpec {
        return buildStyleBuilderMethod(
                styleableClassInfo.elementPackageName,
                String.format(Locale.US, ParisProcessor.STYLE_APPLIER_CLASS_NAME_FORMAT, styleableClassInfo.elementName),
                TypeName.get(styleableClassInfo.viewElementType))
    }

    private fun buildStyleBuilderMethod(styleApplierPackageName: String, styleApplierSimpleName: String, viewParameterTypeName: TypeName): MethodSpec {
        val styleApplierClassName = ClassName.get(
                styleApplierPackageName,
                styleApplierSimpleName)
        val styleBuilderClassName = styleApplierClassName.nestedClass("StyleBuilder")
        return MethodSpec.methodBuilder("styleBuilder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(styleBuilderClassName)
                .addParameter(ParameterSpec.builder(viewParameterTypeName, "view").build())
                .addStatement("return new \$T(new \$T(view))", styleBuilderClassName, styleApplierClassName)
                .build()
    }

    private fun buildAssertStylesMethod(styleableClassesInfo: List<StyleableInfo>): MethodSpec {
        val builder = MethodSpec.methodBuilder("assertStylesContainSameAttributes")
                .addJavadoc("For debugging")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassNames.ANDROID_CONTEXT, "context")

        for (styleableClassInfo in styleableClassesInfo) {
            if (styleableClassInfo.styles.size + styleableClassInfo.newStyles.size > 1) {
                builder.addStatement("\$T \$T = new \$T(context)", styleableClassInfo.elementType, styleableClassInfo.elementType, styleableClassInfo.elementType)

                val styleVarargCodeBuilder = CodeBlock.builder()
                for ((i, style) in styleableClassInfo.styles.withIndex()) {
                    if (i > 0) {
                        styleVarargCodeBuilder.add(", ")
                    }
                    styleVarargCodeBuilder.add("new \$T(\$L)",
                            ParisProcessor.SIMPLE_STYLE_CLASS_NAME, style.androidResourceId.code)
                }
                for ((i, style) in styleableClassInfo.newStyles.withIndex()) {
                    if (i > 0 || styleableClassInfo.styles.isNotEmpty()) {
                        styleVarargCodeBuilder.add(", ")
                    }
                    styleVarargCodeBuilder.add("styleBuilder(\$T).add\$L().build()",
                            styleableClassInfo.elementType, style.elementName.capitalize())
                }

                val assertEqualAttributesCode = CodeBlock.of("\$T.Companion.assertSameAttributes(style(\$T), \$L);\n",
                        ParisProcessor.STYLE_APPLIER_UTILS_CLASS_NAME,
                        styleableClassInfo.elementType,
                        styleVarargCodeBuilder.build())
                builder.addCode(assertEqualAttributesCode)
            }
        }

        return builder.build()
    }
}
