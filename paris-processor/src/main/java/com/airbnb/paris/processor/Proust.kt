package com.airbnb.paris.processor

import com.airbnb.paris.annotations.Format
import com.squareup.javapoet.*
import java.io.IOException
import java.util.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Types

internal object Proust {

    private val CLASS_NAME_FORMAT = "%sStyleApplier"
    private val PARIS_CLASS_NAME = ClassName.get("com.airbnb.paris", "Paris")
    private val STYLE_CLASS_NAME = ClassName.get("com.airbnb.paris", "Style")
    private val STYLE_APPLIER_CLASS_NAME = ClassName.get("com.airbnb.paris", "StyleApplier")
    private val ATTRIBUTE_SET_CLASS_NAME = ClassName.get("android.util", "AttributeSet")
    private val TYPED_ARRAY_WRAPPER_CLASS_NAME = ClassName.get("com.airbnb.paris", "TypedArrayWrapper")
    private val CONFIG_CLASS_NAME = ClassName.get("com.airbnb.paris.Style", "Config")

    fun getClassName(classInfo: StyleableClassInfo): ClassName {
        return ClassName.get(classInfo.packageName, String.format(CLASS_NAME_FORMAT, classInfo.name))
    }

    @Throws(IOException::class)
    fun writeFrom(filer: Filer, typeUtils: Types, styleableClasses: List<StyleableClassInfo>, styleableClassesTree: StyleableClassesTree, rClassName: ClassName) {
        if (styleableClasses.isEmpty()) {
            return
        }

        for (styleableClassInfo in styleableClasses) {
            writeStyleClass(filer, typeUtils, styleableClasses, styleableClassInfo, styleableClassesTree, rClassName)
        }
    }

    @Throws(IOException::class)
    private fun writeStyleClass(filer: Filer, typeUtils: Types, styleableClasses: List<StyleableClassInfo>, classInfo: StyleableClassInfo, styleableClassesTree: StyleableClassesTree, rClassName: ClassName) {
        val className = getClassName(classInfo)

        val adapterTypeBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(STYLE_APPLIER_CLASS_NAME, TypeName.get(classInfo.type)))
                .addMethod(buildConstructorMethod(classInfo))
                .addMethod(buildAttributesMethod(rClassName, classInfo.resourceName))
                .addMethod(buildProcessAttributeMethod(classInfo.attrs))

        for (attrInfo in classInfo.styleableAttrs) {
            val styleApplierClassName = styleableClassesTree.findFirstStyleableSuperClassName(typeUtils, styleableClasses, typeUtils.asElement(attrInfo.type) as TypeElement)
            adapterTypeBuilder.addMethod(buildChangeMethod(attrInfo, styleApplierClassName))
        }

        JavaFile.builder(className.packageName(), adapterTypeBuilder.build())
                .build()
                .writeTo(filer)
    }

    private fun buildConstructorMethod(classInfo: StyleableClassInfo): MethodSpec {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(classInfo.type), "view")
                .addStatement("super(view)")
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

    private fun buildProcessAttributeMethod(attrs: List<AttrInfo>): MethodSpec {
        val methodSpecBuilder = MethodSpec.methodBuilder("processAttribute")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ParameterSpec.builder(STYLE_CLASS_NAME, "style").build())
                .addParameter(ParameterSpec.builder(TYPED_ARRAY_WRAPPER_CLASS_NAME, "a").build())
                .addParameter(ParameterSpec.builder(Integer.TYPE, "index").build())

        var first = true
        for (attr in attrs) {
            val statement = String.format(Locale.US, attr.format.statement, "index")
            methodSpecBuilder.beginControlFlow((if (first) "" else "else ") + "if (index == \$L)", attr.id.code)
            if (attr.isView) {
                assert(attr.format == Format.DEFAULT || attr.format == Format.RESOURCE_ID)
                methodSpecBuilder.addStatement("\$T.change(getView().\$N).apply(a.\$L)", PARIS_CLASS_NAME, attr.name, statement)
            } else if (attr.isMethod) {
                methodSpecBuilder.addStatement("getView().\$N(a.\$L)", attr.name, statement)
            } else {
                methodSpecBuilder.addStatement("getView().\$N = a.\$L", attr.name, statement)
            }
            methodSpecBuilder.endControlFlow()
            first = false
        }

        return methodSpecBuilder.build()
    }

    private fun buildChangeMethod(attrInfo: AttrInfo, styleApplierClassName: ClassName): MethodSpec {
        return MethodSpec.methodBuilder("change" + attrInfo.name.capitalize())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(styleApplierClassName)
                .addParameter(ParameterSpec.builder(TypeName.get(attrInfo.type), "view").build())
                .addStatement("return new \$T(view)", styleApplierClassName)
                .build()
    }

    private fun assert(assertion: Boolean) {
        // TODO
    }
}
