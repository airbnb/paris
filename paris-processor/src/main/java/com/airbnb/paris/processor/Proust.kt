package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Format
import com.google.auto.value.AutoValue
import com.squareup.javapoet.*
import java.io.IOException
import java.util.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror

internal object Proust {

    private val CLASS_NAME_FORMAT = "%sStyle"
    private val PARIS_CLASS_NAME = ClassName.get("com.airbnb.paris", "Paris")
    private val BASE_STYLE_CLASS_NAME = ClassName.get("com.airbnb.paris", "BaseStyle")
    private val ATTRIBUTE_SET_CLASS_NAME = ClassName.get("android.util", "AttributeSet")
    private val TYPED_ARRAY_CLASS_NAME = ClassName.get("android.content.res", "TypedArray")
    private val CONFIG_CLASS_NAME = ClassName.get("com.airbnb.paris.Style", "Config")

    fun getClassName(classInfo: StyleableClassInfo): ClassName {
        return ClassName.get(classInfo.packageName, String.format(CLASS_NAME_FORMAT, classInfo.name))
    }

    @Throws(IOException::class)
    fun writeFrom(filer: Filer, styleableClasses: List<StyleableClassInfo>, rClassName: ClassName) {
        if (styleableClasses.isEmpty()) {
            return
        }

        for (styleableClassInfo in styleableClasses) {
            writeStyleClass(filer, styleableClassInfo, rClassName)
        }
    }

    @Throws(IOException::class)
    private fun writeStyleClass(filer: Filer, classInfo: StyleableClassInfo, rClassName: ClassName) {
        val className = getClassName(classInfo)

        val adapterTypeBuilder = TypeSpec.classBuilder(className)
                .addAnnotation(AutoValue::class.java)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(ParameterizedTypeName.get(BASE_STYLE_CLASS_NAME, TypeName.get(classInfo.type)))
                .addMethod(buildFromMethod(className))
                .addMethod(buildAttributesMethod(rClassName, classInfo.resourceName))
                .addMethod(buildProcessAttributeMethod(classInfo.type, classInfo.attrs))

        JavaFile.builder(className.packageName(), adapterTypeBuilder.build())
                .build()
                .writeTo(filer)
    }

    private fun buildFromMethod(styleClassName: ClassName): MethodSpec {
        return MethodSpec.methodBuilder("from")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(styleClassName)
                .addParameter(ParameterSpec.builder(ATTRIBUTE_SET_CLASS_NAME, "set").build())
                .addParameter(ParameterSpec.builder(Integer.TYPE, "styleRes").build())
                .addParameter(ParameterSpec.builder(CONFIG_CLASS_NAME, "config").build())
                .addStatement("return new AutoValue_\$T(set, styleRes, config)", styleClassName)
                .build()
    }

    private fun buildAttributesMethod(rClassName: ClassName, resourceName: String): MethodSpec {
        return MethodSpec.methodBuilder("attributes")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .returns(ArrayTypeName.of(Integer.TYPE))
                .addStatement("return \$T.styleable.\$L", rClassName, resourceName)
                .build()
    }

    private fun buildProcessAttributeMethod(styleableClassType: TypeMirror, attrs: List<AttrInfo>): MethodSpec {
        val methodSpecBuilder = MethodSpec.methodBuilder("processAttribute")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ParameterSpec.builder(TypeName.get(styleableClassType), "view").build())
                .addParameter(ParameterSpec.builder(TYPED_ARRAY_CLASS_NAME, "a").build())
                .addParameter(ParameterSpec.builder(Integer.TYPE, "index").build())

        var first = true
        for (attr in attrs) {
            val statement = String.format(Locale.US, attr.format.statement, "index")
            methodSpecBuilder.beginControlFlow((if (first) "" else "else ") + "if (index == \$L)", attr.id.code)
            if (attr.isView) {
                assert(attr.format == Format.DEFAULT || attr.format == Format.RESOURCE_ID)
                methodSpecBuilder.addStatement("\$T.change(view.\$N).apply(a.\$L)", PARIS_CLASS_NAME, attr.name, statement)
            } else if (attr.isMethod) {
                methodSpecBuilder.addStatement("view.\$N(a.\$L)", attr.name, statement)
            } else {
                methodSpecBuilder.addStatement("view.\$N = a.\$L", attr.name, statement)
            }
            methodSpecBuilder.endControlFlow()
            first = false
        }

        return methodSpecBuilder.build()
    }

    private fun assert(assertion: Boolean) {
        // TODO
    }
}
