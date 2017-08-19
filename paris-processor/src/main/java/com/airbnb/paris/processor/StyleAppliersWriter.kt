package com.airbnb.paris.processor

import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.utils.ClassNames
import com.airbnb.paris.processor.utils.asTypeElement
import com.airbnb.paris.processor.utils.invoke
import com.grosner.kpoet.*
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import java.io.IOException
import javax.annotation.processing.Filer
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
                constructor(param(TypeName.get(styleableInfo.elementType), "view")) {
                    modifiers(public)
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

                    protected(ArrayTypeName.of(Integer.TYPE), "attributes") {
                        `@`(Override::class)
                        statement("return \$T.styleable.\$L", rClassName, styleableInfo.styleableResourceName)
                    }

                    addMethod(buildProcessAttributesMethod(styleableInfo.styleableFields, styleableInfo.attrs))
                }

                val parentStyleApplierClassName = styleablesTree.findStyleApplier(
                        typeUtils,
                        styleablesInfo,
                        styleableInfo.elementType.asTypeElement(typeUtils).superclass.asTypeElement(typeUtils))
                protected(TypeName.VOID, "applyParent", param(ParisProcessor.STYLE_CLASS_NAME, "style")) {
                    `@`(Override::class)
                    statement("new \$T(getView()).apply(style)", parentStyleApplierClassName)
                }

                if (styleableInfo.dependencies.isNotEmpty()) {
                    protected(TypeName.VOID, "applyDependencies", param(ParisProcessor.STYLE_CLASS_NAME, "style")) {
                        `@`(Override::class)
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
                    public(subStyleApplierClassName, styleableFieldInfo.elementName) {
                        `return`("new \$T(getView().${styleableFieldInfo.elementName})", subStyleApplierClassName)
                    }
                }

                for (styleInfo in styleableInfo.styles) {
                    public(styleApplierClassName, "apply${styleInfo.name.capitalize()}") {
                        `return`("apply(\$L)", styleInfo.androidResourceId.code)
                    }
                }

                this
            }
        }.writeTo(filer)
    }

    private fun buildProcessAttributesMethod(styleableFields: List<StyleableFieldInfo>, attrs: List<AttrInfo>): MethodSpec {
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

    private fun addControlFlow(methodBuilder: MethodSpec.Builder, format: Format,
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

    private fun addStatement(methodSpecBuilder: MethodSpec.Builder, format: Format,
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
}
