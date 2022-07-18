package com.airbnb.paris.proxies

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable


internal val ARBITRARY_COLORS = listOf(Color.GREEN, Color.GREEN, Color.BLUE, Color.TRANSPARENT)
internal val ARBITRARY_COLOR_STATE_LISTS = listOf(
    ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_enabled)
        ), intArrayOf(Color.RED, Color.GREEN)
    )
)
internal val ARBITRARY_COLOR_DRAWABLES = listOf(
    ColorDrawable(Color.RED),
    ColorDrawable(Color.GREEN),
    ColorDrawable(Color.BLUE)
)
internal val ARBITRARY_DIMENSIONS = listOf(Integer.MIN_VALUE, -150, 0, 10, 20, 50, 200, 800, Integer.MAX_VALUE)
internal val ARBITRARY_FLOATS = listOf(-5f, 0f, 8f, 10f, 11.5f, 17f)
internal val ARBITRARY_INTS = listOf(Integer.MIN_VALUE, -5, 0, 1, 2, 3, 5, 15, Integer.MAX_VALUE)
internal val ARBITRARY_STRINGS = listOf("string", "Hello World", "12345")
internal val ARBITRARY_RESOURCE_ID = 2
internal val BOOLS = listOf(true, false)

// TODO What about Dp and ColorInt methods?

internal open class BaseViewMapping<in Builder, Proxy : com.airbnb.paris.proxies.Proxy<Proxy, View>, View : android.view.View, Input : Any?> protected constructor(
    val testValues: List<Input>,
    val attrRes: Int,
    val setProxyFunction: Proxy.(Input) -> Unit,
    val setStyleBuilderValueFunction: Builder.(Input) -> Any,
    val setStyleBuilderResFunction: Builder.(Int) -> Any,
    /**
     * A function which, when called, will assert that the view has been successfully modified
     * by the associated proxy and/or style builder methods
     */
    val assertViewSet: (View, Input) -> Unit
)