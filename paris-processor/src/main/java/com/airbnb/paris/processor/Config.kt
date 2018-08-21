package com.airbnb.paris.processor

import com.airbnb.paris.processor.framework.className

internal const val PARIS_PACKAGE_NAME = "com.airbnb.paris"
internal const val PARIS_MODULES_PACKAGE_NAME = "com.airbnb.paris.modules"
internal const val PARIS_KOTLIN_EXTENSIONS_PACKAGE_NAME = "com.airbnb.paris.extensions"

internal const val PARIS_SIMPLE_CLASS_NAME = "Paris"
internal const val STYLE_APPLIER_SIMPLE_CLASS_NAME_FORMAT = "%sStyleApplier"
internal const val MODULE_SIMPLE_CLASS_NAME_FORMAT = "GeneratedModule_%s"
internal const val EXTENSIONS_FILE_NAME_FORMAT = "%sStyleExtensions"

internal val STYLE_CLASS_NAME = "$PARIS_PACKAGE_NAME.styles.Style".className()
internal val STYLE_APPLIER_CLASS_NAME = "$PARIS_PACKAGE_NAME.StyleApplier".className()
internal val STYLE_BUILDER_CLASS_NAME = "$PARIS_PACKAGE_NAME.StyleBuilder".className()
internal val EXTENDABLE_STYLE_BUILDER_CLASS_NAME = "$PARIS_PACKAGE_NAME.ExtendableStyleBuilder".className()
internal val STYLE_APPLIER_UTILS_CLASS_NAME = "$PARIS_PACKAGE_NAME.StyleApplierUtils".className()
internal val TYPED_ARRAY_WRAPPER_CLASS_NAME = "$PARIS_PACKAGE_NAME.typed_array_wrappers.TypedArrayWrapper".className()
internal val STYLE_BUILDER_FUNCTION_CLASS_NAME = "$PARIS_PACKAGE_NAME.utils.StyleBuilderFunction".className()
internal val CONTEXT_EXTENSIONS_CLASS_NAME = "$PARIS_PACKAGE_NAME.utils.ContextExtensionsKt".className()
internal val RESOURCES_EXTENSIONS_CLASS_NAME = "$PARIS_PACKAGE_NAME.utils.ResourcesExtensionsKt".className()
internal val SPANNABLE_BUILDER_CLASS_NAME = "$PARIS_PACKAGE_NAME.spannables.SpannableBuilder".className()
internal val PROXY_CLASS_NAME = "$PARIS_PACKAGE_NAME.proxies.Proxy".className()
