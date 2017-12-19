package com.airbnb.paris.processor

import com.airbnb.paris.processor.utils.*
import com.squareup.javapoet.*
import java.io.*
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.element.*

internal object ParisWriter {

    @Throws(IOException::class)
    internal fun writeFrom(filer: Filer, parisClassPackageName: String, styleableClassesInfo: List<StyleableInfo>, externalStyleableClassesInfo: List<BaseStyleableInfo>) {
        val parisTypeBuilder = TypeSpec.classBuilder(PARIS_SIMPLE_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

        val sortedStyleableClassesInfo = (styleableClassesInfo + externalStyleableClassesInfo).sortedBy {
            it.elementName
        }
        for (styleableClassInfo in sortedStyleableClassesInfo) {
            parisTypeBuilder.addMethod(buildStyleMethod(styleableClassInfo))
            parisTypeBuilder.addMethod(buildStyleBuilderMethod(styleableClassInfo))
        }

        parisTypeBuilder.addMethod(buildAssertStylesMethod(styleableClassesInfo))

        JavaFile.builder(parisClassPackageName, parisTypeBuilder.build())
                .build()
                .writeTo(filer)
    }

    private fun getStyleApplierClassName(styleableClassInfo: BaseStyleableInfo): ClassName {
        return ClassName.get(
                styleableClassInfo.elementPackageName,
                String.format(Locale.US, STYLE_APPLIER_SIMPLE_CLASS_NAME_FORMAT, styleableClassInfo.elementName)
        )
    }

    private fun getStyleBuilderClassName(styleableClassInfo: BaseStyleableInfo): ClassName =
            getStyleApplierClassName(styleableClassInfo).nestedClass("StyleBuilder")

    private fun buildStyleMethod(styleableClassInfo: BaseStyleableInfo): MethodSpec {
        return buildStyleMethod(
                getStyleApplierClassName(styleableClassInfo),
                TypeName.get(styleableClassInfo.viewElementType))
    }

    private fun buildStyleMethod(styleApplierClassName: ClassName, viewParameterTypeName: TypeName): MethodSpec {
        return MethodSpec.methodBuilder("style")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(styleApplierClassName)
                .addParameter(ParameterSpec.builder(viewParameterTypeName, "view").build())
                .addStatement("return new \$T(view)", styleApplierClassName)
                .build()
    }

    private fun buildStyleBuilderMethod(styleableClassInfo: BaseStyleableInfo): MethodSpec {
        return buildStyleBuilderMethod(
                getStyleApplierClassName(styleableClassInfo),
                TypeName.get(styleableClassInfo.viewElementType))
    }

    private fun buildStyleBuilderMethod(styleApplierClassName: ClassName,  viewParameterTypeName: TypeName): MethodSpec {
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
            if (styleableClassInfo.styles.size > 1) {
                builder.addStatement("\$T \$T = new \$T(context)", styleableClassInfo.elementType, styleableClassInfo.elementType, styleableClassInfo.elementType)

                val styleVarargCodeBuilder = CodeBlock.builder()
                for ((i, style) in styleableClassInfo.styles.withIndex()) {
                    if (i > 0) {
                        styleVarargCodeBuilder.add(", ")
                    }
                    styleVarargCodeBuilder.add("new \$T().add\$L().build()",
                            getStyleBuilderClassName(styleableClassInfo), style.formattedName)
                }

                val assertEqualAttributesCode = CodeBlock.of("\$T.Companion.assertSameAttributes(style(\$T), \$L);\n",
                        STYLE_APPLIER_UTILS_CLASS_NAME,
                        styleableClassInfo.elementType,
                        styleVarargCodeBuilder.build())
                builder.addCode(assertEqualAttributesCode)
            }
        }

        return builder.build()
    }
}
