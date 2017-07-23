package com.airbnb.paris.processor.utils

import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

val Elements.VIEW_TYPE: TypeElement get() = this.getTypeElement("android.view.View")
