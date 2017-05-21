package com.airbnb.paris.processor

import com.airbnb.paris.processor.android_resource_scanner.AndroidResourceId

// TODO  Make sure the name doesn't contain illegal characters?
internal class StyleInfo constructor(
        val name: String,
        val androidResourceId: AndroidResourceId)
