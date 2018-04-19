package com.airbnb.paris.processor.writers

import com.airbnb.paris.processor.framework.SkyKotlinFile
import com.airbnb.paris.processor.models.BaseStyleableInfo


internal class KotlinStyleExtensionsFile(
    val styleable: BaseStyleableInfo
) : SkyKotlinFile(styleable.elementPackageName, "${styleable.elementName}StyleExtensions", {

})