package com.airbnb.paris.processor

import com.google.auto.value.AutoValue
import com.squareup.javapoet.*
import java.io.IOException
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror

internal object Proust {

    private val CLASS_NAME_FORMAT = "%sStyle"
    private val BASE_STYLE_CLASS_NAME = ClassName.get("com.airbnb.paris", "BaseStyle")
    private val ATTRIBUTE_SET_CLASS_NAME = ClassName.get("android.util", "AttributeSet")
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
                .addMethod(buildTargetClassMethod(classInfo.type))
                .addMethod(buildAttributesMethod(rClassName, classInfo.resourceName))

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

    private fun buildTargetClassMethod(styleableClassType: TypeMirror): MethodSpec {
        return MethodSpec.methodBuilder("targetClass")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .returns(ParameterizedTypeName.get(ClassName.get(Class::class.java), TypeName.get(styleableClassType)))
                .addStatement("return \$T.class", styleableClassType)
                .build()
    }

    private fun buildAttributesMethod(rClassName: ClassName, resourceName: String): MethodSpec {
        return MethodSpec.methodBuilder("attributes")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .returns(ArrayTypeName.get(Integer.TYPE))
                .addStatement("return \$T.styleable.\$L", rClassName, resourceName)
                .build()
    }
}
