package com.airbnb.paris.processor.writers

import com.airbnb.paris.processor.STYLE_CLASS_NAME
import com.airbnb.paris.processor.framework.SkyKotlinFile
import com.airbnb.paris.processor.framework.toKPoet
import com.airbnb.paris.processor.models.BaseStyleableInfo
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asTypeName


internal class KotlinStyleExtensionsFile(
    val styleable: BaseStyleableInfo
) : SkyKotlinFile(styleable.elementPackageName, "${styleable.viewElementType}StyleExtensions", {


    //
//    fun View.style(init: ViewStyleApplier.StyleBuilder.() -> Unit) {
//        ViewStyleApplier.StyleBuilder().let {
//            it.init()
//            it.applyTo(this)
//        }
//    }
//
//    // Linked style
//    fun View.styleGreen() {
//        ViewStyleApplier(this).apply(R.style.Green)
//    }


    //    fun View.style(style: Style) {
//        ViewStyleApplier(this).apply(style)
//    }
    addFunction(
        FunSpec.builder("style")
            .receiver(styleable.viewElementType.asTypeName())
            .addParameter("style", STYLE_CLASS_NAME.toKPoet())
            .addStatement("%T(this).apply(style)", styleable.styleApplierClassName().toKPoet())
            .build()
    )

})