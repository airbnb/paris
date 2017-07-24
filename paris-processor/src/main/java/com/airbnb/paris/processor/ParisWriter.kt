package com.airbnb.paris.processor

import com.airbnb.paris.processor.utils.ClassNames
import com.airbnb.paris.processor.utils.className
import com.grosner.kpoet.*
import com.squareup.javapoet.*
import java.io.IOException
import java.util.*
import javax.annotation.processing.Filer

internal object ParisWriter {

    @Throws(IOException::class)
    internal fun writeFrom(filer: Filer, styleableClassesInfo: List<StyleableInfo>) {
        `package paris` {
            `public final class Paris extends ParisBase` {

                for ((styleApplierQualifiedName, viewQualifiedName) in ParisProcessor.BUILT_IN_STYLE_APPLIERS) {
                    val styleApplierClassName = styleApplierQualifiedName.className()
                    `public static void style(view)`(
                            styleApplierClassName.packageName(),
                            styleApplierClassName.simpleName(),
                            viewQualifiedName.className())
                }

                for (styleableClassInfo in styleableClassesInfo) {
                    `public static void style(view)`(styleableClassInfo)
                }

                `public static void assertStylesContainSameAttributes(context)`(styleableClassesInfo)

                this
            }
        }.writeTo(filer)
    }
}

private fun `package paris`(function: () -> TypeSpec): JavaFile {
    return javaFile(ParisProcessor.PARIS_CLASS_NAME.packageName(), function = function)
}

private fun `public final class Paris extends ParisBase`(function: TypeMethod): TypeSpec {
    return `public final class`(ParisProcessor.PARIS_CLASS_NAME.simpleName()) {
        extends(ParisProcessor.PARIS_BASE_CLASS_NAME)
        function()
    }
}

private fun TypeSpec.Builder.`public static void style(view)`(styleableClassInfo: StyleableInfo): TypeSpec.Builder {
    return `public static void style(view)`(
            styleableClassInfo.elementPackageName,
            String.format(Locale.US, ParisProcessor.STYLE_APPLIER_CLASS_NAME_FORMAT, styleableClassInfo.elementName),
            TypeName.get(styleableClassInfo.elementType))
}

private fun TypeSpec.Builder.`public static void style(view)`(styleApplierPackageName: String, styleApplierSimpleName: String, viewParameterTypeName: TypeName): TypeSpec.Builder {
    val styleApplierClassName = ClassName.get(styleApplierPackageName, styleApplierSimpleName)
    return `public static`(styleApplierClassName, "style", param(viewParameterTypeName, "view")) {
        `return`("process(new \$T(view))", styleApplierClassName)
    }
}

private fun TypeSpec.Builder.`public static void assertStylesContainSameAttributes(context)`(styleableClassesInfo: List<StyleableInfo>): TypeSpec.Builder {
    return `public static`(TypeName.VOID, "assertStylesContainSameAttributes", param(ClassNames.ANDROID_CONTEXT, "context")) {
        javadoc("For debugging")

        for (styleableClassInfo in styleableClassesInfo) {
            if (styleableClassInfo.styles.size > 1) {
                statement("\$T \$T = new \$T(context)", styleableClassInfo.elementType, styleableClassInfo.elementType, styleableClassInfo.elementType)

                val styleVarargCodeBuilder = CodeBlock.builder()
                for ((i, style) in styleableClassInfo.styles.withIndex()) {
                    if (i > 0) {
                        styleVarargCodeBuilder.add(", ")
                    }
                    styleVarargCodeBuilder.add("new \$T(\$L)",
                            ParisProcessor.STYLE_CLASS_NAME, style.androidResourceId.code)
                }

                val assertEqualAttributesCode = CodeBlock.of("\$T.Companion.assertSameAttributes(style(\$T), \$L);\n",
                        ParisProcessor.STYLE_APPLIER_UTILS_CLASS_NAME,
                        styleableClassInfo.elementType,
                        styleVarargCodeBuilder.build())
                addCode(assertEqualAttributesCode)
            }
        }

        this
    }
}
