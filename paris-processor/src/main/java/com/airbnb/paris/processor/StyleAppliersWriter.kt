package com.airbnb.paris.processor

import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId
import com.airbnb.paris.processor.utils.ClassNames
import com.airbnb.paris.processor.utils.asTypeElement
import com.airbnb.paris.processor.utils.invoke
import com.grosner.kpoet.*
import com.squareup.javapoet.*
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

        val styleTypeBuilder = `public final class`(styleApplierClassName.simpleName()) {
            extends(ParameterizedTypeName.get(ParisProcessor.STYLE_APPLIER_CLASS_NAME, styleApplierClassName, TypeName.get(styleableInfo.elementType)))
            constructor(param(TypeName.get(styleableInfo.elementType), "view")) {
                modifiers(public)
                statement("super(view)")
            }
        }.toBuilder()

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

            styleTypeBuilder {
                protected(ArrayTypeName.of(Integer.TYPE), "attributes") {
                    `@`(Override::class)
                    statement("return \$T.styleable.\$L", rClassName, styleableInfo.styleableResourceName)
                }

                addMethod(buildProcessAttributesMethod(styleableInfo.styleableFields, styleableInfo.attrs))
            }
        }

        val parentStyleApplierClassName = styleablesTree.findStyleApplier(
                typeUtils,
                styleablesInfo,
                styleableInfo.elementType.asTypeElement(typeUtils).superclass.asTypeElement(typeUtils))
        styleTypeBuilder.addMethod(buildApplyParentMethod(parentStyleApplierClassName))

        if (styleableInfo.dependencies.isNotEmpty()) {
            styleTypeBuilder.addMethod(buildApplyDependenciesMethod(styleableInfo))
        }

        for (styleableFieldInfo in styleableInfo.styleableFields) {
            val subStyleApplierClassName = styleablesTree.findStyleApplier(
                    typeUtils,
                    styleablesInfo,
                    styleableFieldInfo.elementType.asTypeElement(typeUtils))
            styleTypeBuilder {
                public(subStyleApplierClassName, styleableFieldInfo.elementName) {
                    `return`("new \$T(getView().${styleableFieldInfo.elementName})", subStyleApplierClassName)
                }
            }
        }

        for (styleInfo in styleableInfo.styles) {
            styleTypeBuilder {
                public(styleApplierClassName, "apply${styleInfo.name.capitalize()}") {
                    `return`("apply(\$L)", styleInfo.androidResourceId.code)
                }
            }
        }

        JavaFile.builder(styleApplierClassName.packageName(), styleTypeBuilder.build())
                .build()
                .writeTo(filer)
    }

    private fun buildApplyParentMethod(parentStyleApplierClassName: ClassName): MethodSpec {
        return protected(TypeName.VOID, "applyParent", param(ParisProcessor.STYLE_CLASS_NAME, "style")) {
            `@`(Override::class)
            statement("new \$T(getView()).apply(style)", parentStyleApplierClassName)
        }
    }

    private fun buildApplyDependenciesMethod(classInfo: StyleableInfo): MethodSpec {
        val methodBuilder = protected(TypeName.VOID, "applyDependencies", param(ParisProcessor.STYLE_CLASS_NAME, "style")) {
            `@`(Override::class)
        }.toBuilder()

        for (dependency in classInfo.dependencies) {
            methodBuilder.statement("new \$T(getView()).apply(style)", dependency)
        }

        return methodBuilder.build()
    }

    private fun buildProcessAttributesMethod(styleableFields: List<StyleableFieldInfo>, attrs: List<AttrInfo>): MethodSpec {
        val methodBuilder = protected(TypeName.VOID, "processAttributes",
                param(ParisProcessor.STYLE_CLASS_NAME, "style"), param(ParisProcessor.TYPED_ARRAY_WRAPPER_CLASS_NAME, "a")) {
            `@`(Override::class)
            statement("\$T res = getView().getContext().getResources()", ClassNames.ANDROID_RESOURCES)
        }.toBuilder()

        if (styleableFields.isNotEmpty()) {
            methodBuilder.statement("\$T subStyle", ParisProcessor.STYLE_CLASS_NAME)
        }

        for (styleableField in styleableFields) {
            addControlFlow(methodBuilder, Format.RESOURCE_ID, styleableField.elementName,
                    styleableField.styleableResId, styleableField.defaultValueResId, true)
        }

        for (attr in attrs) {
            addControlFlow(methodBuilder, attr.targetFormat, attr.elementName,
                    attr.styleableResId, attr.defaultValueResId, false)
        }

        return methodBuilder.build()
    }

    private fun addControlFlow(methodBuilder: MethodSpec.Builder, format: Format,
                               elementName: String, styleableResId: AndroidResourceId,
                               defaultValueResId: AndroidResourceId?, isElementStyleable: Boolean) {
        methodBuilder.beginControlFlow("if (a.hasValue(\$L))", styleableResId.code)
        addStatement(methodBuilder, format, elementName, false, format.typedArrayMethodStatement(), styleableResId, isElementStyleable)
        methodBuilder.endControlFlow()

        if (defaultValueResId != null) {
            methodBuilder.beginControlFlow("else")
            addStatement(methodBuilder, format, elementName, true, format.resourcesMethodStatement(), defaultValueResId, isElementStyleable)
            methodBuilder.endControlFlow()
        }
    }

    private fun addStatement(methodSpecBuilder: MethodSpec.Builder, format: Format,
                             elementName: String, isForDefaultValue: Boolean, statement: String,
                             androidResourceId: AndroidResourceId, isElementStyleable: Boolean) {
        val from = if (isForDefaultValue) "res" else "a"
        if (isElementStyleable) {
            methodSpecBuilder {
                statement("subStyle = new \$T($from.$statement)", ParisProcessor.STYLE_CLASS_NAME, androidResourceId.code)
                statement("subStyle.setDebugListener(style.getDebugListener())")
                statement("\$T.style(getView().\$N).apply(subStyle)", ParisProcessor.PARIS_CLASS_NAME, elementName)
            }
        } else {
            if (isForDefaultValue && format == Format.RESOURCE_ID) {
                // The parameter is the resource id
                methodSpecBuilder.statement("getView().\$N(\$L)", elementName, androidResourceId.code)
            } else {
                methodSpecBuilder.statement("getView().\$N($from.$statement)", elementName, androidResourceId.code)
            }
        }
    }
}
