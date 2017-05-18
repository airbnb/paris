package com.airbnb.paris.processor

import com.squareup.javapoet.*
import java.io.IOException
import java.util.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

internal object ParisWriter {

    private val STYLE_APPLIER_CLASS_NAME_FORMAT = "%sStyleApplier"
    private val PARIS_CLASS_NAME = ClassName.get("com.airbnb.paris", "Paris")

    @Throws(IOException::class)
    fun writeFrom(filer: Filer, styleableClassesInfo: List<StyleableClassInfo>) {
        if (styleableClassesInfo.isEmpty()) {
            return
        }

        val parisTypeBuilder = TypeSpec.classBuilder(PARIS_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(buildChangeMethod("com.airbnb.paris", "ViewStyleApplier", ClassName.get("android.view", "View")))
                .addMethod(buildChangeMethod("com.airbnb.paris", "TextViewStyleApplier", ClassName.get("android.widget", "TextView")))

        for (styleableClassInfo in styleableClassesInfo) {
            parisTypeBuilder.addMethod(buildChangeMethod(styleableClassInfo))
        }

        JavaFile.builder(PARIS_CLASS_NAME.packageName(), parisTypeBuilder.build())
                .build()
                .writeTo(filer)
    }

    private fun buildChangeMethod(styleableClassInfo: StyleableClassInfo): MethodSpec {
        return buildChangeMethod(
                styleableClassInfo.packageName,
                String.format(Locale.US, STYLE_APPLIER_CLASS_NAME_FORMAT, styleableClassInfo.name),
                TypeName.get(styleableClassInfo.type))
    }

    private fun buildChangeMethod(styleApplierPackageName: String, styleApplierSimpleName: String, viewParameterTypeName: TypeName): MethodSpec {
        val styleApplierClassName = ClassName.get(
                styleApplierPackageName,
                styleApplierSimpleName)
        return MethodSpec.methodBuilder("change")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(styleApplierClassName)
                .addParameter(ParameterSpec.builder(viewParameterTypeName, "view").build())
                .addStatement("return new \$T(view)", styleApplierClassName)
                .build()
    }
}
