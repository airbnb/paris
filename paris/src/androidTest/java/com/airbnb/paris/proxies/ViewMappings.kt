package com.airbnb.paris.proxies

import android.view.*
import com.airbnb.paris.proxies.ViewProxyStyleApplier.*

internal class ViewMapping<I> private constructor(
        testValues: List<I>,
        attrRes: Int,
        setProxyFunction: ViewProxy.(I) -> Unit,
        setStyleBuilderValueFunction: BaseStyleBuilder<*, *>.(I) -> Any,
        setStyleBuilderResFunction: BaseStyleBuilder<*, *>.(Int) -> Any,
        /**
         * A function which, when called, will assert that the view has been successfully modified
         * by the associated proxy and/or style builder methods
         */
        assertViewSet: (View, I) -> Unit) :
        BaseViewMapping<BaseStyleBuilder<*, *>, ViewProxy, View, I>(
                testValues,
                attrRes,
                setProxyFunction,
                setStyleBuilderValueFunction,
                setStyleBuilderResFunction,
                assertViewSet) {

    companion object {

        fun <I> withCustomAssert(
                testValues: List<I>,
                attrRes: Int,
                setProxyFunction: ViewProxy.(I) -> Unit,
                setStyleBuilderValueFunction: BaseStyleBuilder<*, *>.(I) -> Any,
                setStyleBuilderResFunction: BaseStyleBuilder<*, *>.(Int) -> Any,
                assertViewSet: (View, I) -> Unit): ViewMapping<I> {
            return ViewMapping(
                    testValues,
                    attrRes,
                    setProxyFunction,
                    setStyleBuilderValueFunction,
                    setStyleBuilderResFunction,
                    assertViewSet)
        }

        fun <I> withAssertEquals(
                testValues: List<I>,
                attrRes: Int,
                setProxyFunction: ViewProxy.(I) -> Unit,
                setStyleBuilderValueFunction: BaseStyleBuilder<*, *>.(I) -> Any,
                setStyleBuilderResFunction: BaseStyleBuilder<*, *>.(Int) -> Any,
                getViewFunction: (View) -> I): ViewMapping<I> {
            return withCustomAssert(
                    testValues,
                    attrRes,
                    setProxyFunction,
                    setStyleBuilderValueFunction,
                    setStyleBuilderResFunction,
                    { View, input -> junit.framework.Assert.assertEquals(input, getViewFunction(View)) })
        }
    }
}

internal val VIEW_MAPPINGS = ArrayList<ViewMapping<*>>().apply {

    // alpha
    add(ViewMapping.withAssertEquals(
            listOf(-1f, -.4f, -.33333f, 0f, .2229432489f, .666f, 1f, 2f),
            android.R.attr.alpha,
            ViewProxy::setAlpha,
            BaseStyleBuilder<*, *>::alpha,
            BaseStyleBuilder<*, *>::alpha,
            { it.alpha }
    ))

    // drawableBottom
    add(ViewMapping.withAssertEquals(
            (0..2).toList(),
            android.R.attr.visibility,
            ViewProxy::setVisibility,
            BaseStyleBuilder<*, *>::visibility,
            BaseStyleBuilder<*, *>::visibilityRes,
            {
                mapOf(
                        View.VISIBLE to 0,
                        View.INVISIBLE to 1,
                        View.GONE to 2
                )[it.visibility]!!
            }
    ))

    // elevation
    add(ViewMapping.withAssertEquals(
            ARBITRARY_DIMENSIONS,
            android.R.attr.elevation,
            ViewProxy::setElevation,
            BaseStyleBuilder<*, *>::elevation,
            BaseStyleBuilder<*, *>::elevationRes,
            { it.elevation.toInt() }
    ))

    // foreground
    add(ViewMapping.withAssertEquals(
            ARBITRARY_COLOR_DRAWABLES,
            android.R.attr.foreground,
            ViewProxy::setForeground,
            BaseStyleBuilder<*, *>::foreground,
            BaseStyleBuilder<*, *>::foreground,
            { it.foreground }
    ))

    // minHeight
    add(ViewMapping.withAssertEquals(
            ARBITRARY_DIMENSIONS,
            android.R.attr.minHeight,
            ViewProxy::setMinHeight,
            BaseStyleBuilder<*, *>::minHeight,
            BaseStyleBuilder<*, *>::minHeightRes,
            { it.minimumHeight }
    ))

    // minWidth
    add(ViewMapping.withAssertEquals(
            ARBITRARY_DIMENSIONS,
            android.R.attr.minWidth,
            ViewProxy::setMinWidth,
            BaseStyleBuilder<*, *>::minWidth,
            BaseStyleBuilder<*, *>::minWidthRes,
            { it.minimumWidth }
    ))
}
