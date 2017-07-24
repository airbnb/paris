package com.airbnb.paris.processor

import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.utils.ClassNames
import com.airbnb.paris.processor.utils.asTypeElement
import com.airbnb.paris.processor.utils.invoke
import com.grosner.kpoet.*
import com.squareup.javapoet.*
import java.io.IOException
import javax.annotation.processing.Filer
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Types

// TODO  Add @UiThread annotation to StyleApplier classes
internal object StyleAppliersWriter {

    @Throws(IOException::class)
    fun writeFrom(filer: Filer, typeUtils: Types, styleablesInfo: List<StyleableInfo>) {
        val styleableClassesTree = StyleablesTree()

        for (styleableInfo in styleablesInfo) {
            writeStyleApplier(filer, typeUtils, styleablesInfo, styleableInfo, styleableClassesTree)
        }
    }

    private fun writeStyleApplier(filer: Filer, typeUtils: Types, styleablesInfo: List<StyleableInfo>, styleableInfo: StyleableInfo, styleablesTree: StyleablesTree) {
        val styleApplierClassName = styleableInfo.styleApplierClassName()

        javaFile(styleApplierClassName.packageName()) {
            `public final class`(styleApplierClassName.simpleName()) {
                extends(ParameterizedTypeName.get(ParisProcessor.STYLE_APPLIER_CLASS_NAME, styleApplierClassName, TypeName.get(styleableInfo.elementType)))

                `public StyleApplier(view)`(styleableInfo.elementType) {
                    statement("super(view)")
                }

                if (!styleableInfo.styleableResourceName.isEmpty()) {
                    // Use an arbitrary AndroidResourceId to get R's ClassName. Per the StyleableInfo doc
                    // it's safe to assume that either styleableFields or attrs won't be empty if
                    // styleableResourceName isn't either
                    val arbitraryResId = if (!styleableInfo.styleableFields.isEmpty()) {
                        styleableInfo.styleableFields[0].styleableResId
                    } else {
                        styleableInfo.attrs[0].styleableResId
                    }
                    val rClassName = arbitraryResId.className!!.enclosingClassName()

                    `protected int attributes()` {
                        statement("return \$T.styleable.\$L", rClassName, styleableInfo.styleableResourceName)
                    }

                    `protected void processAttributes(style, typedArrayWrapper)`(styleableInfo.styleableFields, styleableInfo.attrs)
                }

                val parentStyleApplierClassName = styleablesTree.findStyleApplier(
                        typeUtils,
                        styleablesInfo,
                        styleableInfo.elementType.asTypeElement(typeUtils).superclass.asTypeElement(typeUtils))
                `protected void applyParent(style)` {
                    statement("new \$T(getView()).apply(style)", parentStyleApplierClassName)
                }

                if (styleableInfo.dependencies.isNotEmpty()) {
                    `protected void applyDependencies(style)` {
                        for (dependency in styleableInfo.dependencies) {
                            statement("new \$T(getView()).apply(style)", dependency)
                        }
                        this
                    }
                }

                for (styleableFieldInfo in styleableInfo.styleableFields) {
                    val subStyleApplierClassName = styleablesTree.findStyleApplier(
                            typeUtils,
                            styleablesInfo,
                            styleableFieldInfo.elementType.asTypeElement(typeUtils))
                    `public StyleApplier styleableField()`(subStyleApplierClassName, styleableFieldInfo.elementName) {
                        `return`("new \$T(getView().${styleableFieldInfo.elementName})", subStyleApplierClassName)
                    }
                }

                for (styleInfo in styleableInfo.styles) {
                    `public StyleApplier applyStyle()`(styleApplierClassName, styleInfo.name) {
                        `return`("apply(\$L)", styleInfo.androidResourceId.code)
                    }
                }

                this
            }
        }.writeTo(filer)
    }
}

private fun TypeSpec.Builder.`public StyleApplier(view)`(customViewType: TypeMirror, function: MethodSpec.Builder.() -> MethodSpec.Builder): TypeSpec.Builder {
    return constructor(param(TypeName.get(customViewType), "view")) {
        modifiers(public)
        function()
    }
}

private fun TypeSpec.Builder.`protected int attributes()`(function: MethodSpec.Builder.() -> MethodSpec.Builder): TypeSpec.Builder {
    return protected(ArrayTypeName.of(Integer.TYPE), "attributes") {
        `@`(Override::class)
        function()
    }
}

private fun TypeSpec.Builder.`protected void processAttributes(style, typedArrayWrapper)`(styleableFields: List<StyleableFieldInfo>, attrs: List<AttrInfo>): TypeSpec.Builder {
    fun addStatement(methodSpecBuilder: MethodSpec.Builder, format: Format,
                     elementName: String, isForDefaultValue: Boolean, statement: String,
                     androidResourceId: AndroidResourceId, isElementStyleable: Boolean): MethodSpec.Builder {
        val from = if (isForDefaultValue) "res" else "a"
        return methodSpecBuilder {
            if (isElementStyleable) {
                statement("subStyle = new \$T($from.$statement)", ParisProcessor.STYLE_CLASS_NAME, androidResourceId.code)
                statement("subStyle.setDebugListener(style.getDebugListener())")
                statement("\$T.style(getView().\$N).apply(subStyle)", ParisProcessor.PARIS_CLASS_NAME, elementName)
            } else {
                if (isForDefaultValue && format == Format.RESOURCE_ID) {
                    // The parameter is the resource id
                    statement("getView().\$N(\$L)", elementName, androidResourceId.code)
                } else {
                    statement("getView().\$N($from.$statement)", elementName, androidResourceId.code)
                }
            }
            this
        }
    }

    fun addControlFlow(methodBuilder: MethodSpec.Builder, format: Format,
                               elementName: String, styleableResId: AndroidResourceId,
                               defaultValueResId: AndroidResourceId?, isElementStyleable: Boolean) {
        methodBuilder {
            `if` ("a.hasValue(\$L)", styleableResId.code) {
                addStatement(this, format, elementName, false, format.typedArrayMethodStatement(), styleableResId, isElementStyleable)
            }
            if (defaultValueResId != null) {
                `else` {
                    addStatement(this, format, elementName, true, format.resourcesMethodStatement(), defaultValueResId, isElementStyleable)
                }
            }
            end()
            this
        }
    }

    return protected(TypeName.VOID, "processAttributes",
            param(ParisProcessor.STYLE_CLASS_NAME, "style"), param(ParisProcessor.TYPED_ARRAY_WRAPPER_CLASS_NAME, "a")) {
        `@`(Override::class)

        statement("\$T res = getView().getContext().getResources()", ClassNames.ANDROID_RESOURCES)

        if (styleableFields.isNotEmpty()) {
            statement("\$T subStyle", ParisProcessor.STYLE_CLASS_NAME)
        }

        for (styleableField in styleableFields) {
            addControlFlow(this, Format.RESOURCE_ID, styleableField.elementName,
                    styleableField.styleableResId, styleableField.defaultValueResId, true)
        }

        for (attr in attrs) {
            addControlFlow(this, attr.targetFormat, attr.elementName,
                    attr.styleableResId, attr.defaultValueResId, false)
        }

        this
    }
}

private fun TypeSpec.Builder.`protected void applyParent(style)`(function: MethodSpec.Builder.() -> MethodSpec.Builder): TypeSpec.Builder {
    return protected(TypeName.VOID, "applyParent", param(ParisProcessor.STYLE_CLASS_NAME, "style")) {
        `@`(Override::class)
        function()
    }
}

private fun TypeSpec.Builder.`protected void applyDependencies(style)`(function: MethodSpec.Builder.() -> MethodSpec.Builder): TypeSpec.Builder {
    return protected(TypeName.VOID, "applyDependencies", param(ParisProcessor.STYLE_CLASS_NAME, "style")) {
        `@`(Override::class)
        function()
    }
}

private fun TypeSpec.Builder.`public StyleApplier styleableField()`(subStyleApplierClassName: ClassName, styleableFieldName: String, function: MethodSpec.Builder.() -> MethodSpec.Builder): TypeSpec.Builder {
    return public(subStyleApplierClassName, styleableFieldName) {
        function()
    }
}

private fun TypeSpec.Builder.`public StyleApplier applyStyle()`(styleApplierClassName: ClassName, styleName: String, function: MethodSpec.Builder.() -> MethodSpec.Builder): TypeSpec.Builder {
    return public(styleApplierClassName, "apply${styleName.capitalize()}") {
        function()
    }
}
