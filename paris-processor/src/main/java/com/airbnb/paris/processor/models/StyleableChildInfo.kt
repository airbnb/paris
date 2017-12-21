package com.airbnb.paris.processor.models

import com.airbnb.paris.annotations.*
import com.airbnb.paris.processor.*
import com.airbnb.paris.processor.android_resource_scanner.*
import com.airbnb.paris.processor.framework.*
import com.airbnb.paris.processor.framework.errors.*
import com.airbnb.paris.processor.framework.models.*
import javax.lang.model.element.*

internal class StyleableChildInfoExtractor
    : SkyFieldModelFactory<StyleableChildInfo>(StyleableChild::class.java) {

    override fun elementToModel(element: Element): StyleableChildInfo? {
        check(element.isNotPrivate() && element.isNotProtected(), element) {
            "Fields annotated with @StyleableChild can't be private or protected"
        }

        val attr = element.getAnnotation(StyleableChild::class.java)
        val styleableResId = getResourceId(Attr::class.java, element, attr.value)
        var defaultValueResId: AndroidResourceId? = null
        if (attr.defaultValue != -1) {
            defaultValueResId = getResourceId(Attr::class.java, element, attr.defaultValue)
        }

        return StyleableChildInfo(
                element,
                styleableResId,
                defaultValueResId)
    }
}

internal class StyleableChildInfo(
        element: Element,
        val styleableResId: AndroidResourceId,
        val defaultValueResId: AndroidResourceId?
) : SkyFieldModel(element)
