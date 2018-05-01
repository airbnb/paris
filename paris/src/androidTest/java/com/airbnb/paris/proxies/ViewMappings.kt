package com.airbnb.paris.proxies

import android.os.Build
import android.view.View
import android.view.ViewStyleApplier.BaseStyleBuilder
import junit.framework.Assert.assertEquals

internal class ViewMapping<I : Any> private constructor(
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

        fun <I : Any> withCustomAssert(
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

        fun <I : Any> withAssertEquals(
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
                    { View, input -> assertEquals(input, getViewFunction(View)) })
        }
    }
}

internal val VIEW_SETUPS = listOf(
        { view: View -> view.layoutDirection = View.LAYOUT_DIRECTION_LTR },
        { view: View -> view.layoutDirection = View.LAYOUT_DIRECTION_RTL }
)

internal val VIEW_MAPPINGS = ArrayList<ViewMapping<*>>().apply {

    // alpha
    add(ViewMapping.withAssertEquals(
            listOf(-1f, -.4f, -.33333f, 0f, .2229432489f, .666f, 1f, 2f),
            android.R.attr.alpha,
            ViewProxy::setAlpha,
            BaseStyleBuilder<*, *>::alpha,
            BaseStyleBuilder<*, *>::alphaRes,
            { it.alpha }
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        add(ViewMapping.withAssertEquals(
                ARBITRARY_COLOR_DRAWABLES,
                android.R.attr.foreground,
                ViewProxy::setForeground,
                BaseStyleBuilder<*, *>::foreground,
                BaseStyleBuilder<*, *>::foregroundRes,
                { it.foreground }
        ))
    }

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

    // paddingEnd
    add(ViewMapping.withAssertEquals(
            ARBITRARY_DIMENSIONS,
            android.R.attr.paddingEnd,
            ViewProxy::setPaddingEnd,
            BaseStyleBuilder<*, *>::paddingEnd,
            BaseStyleBuilder<*, *>::paddingEndRes,
            { it.paddingEnd }
    ))

    // paddingHorizontal
    add(ViewMapping.withCustomAssert(
            ARBITRARY_DIMENSIONS,
           android.R.attr.paddingHorizontal,
            ViewProxy::setPaddingHorizontal,
            BaseStyleBuilder<*, *>::paddingHorizontal,
            BaseStyleBuilder<*, *>::paddingHorizontalRes,
            { view, input ->
                assertEquals(input, view.paddingEnd)
                assertEquals(input, view.paddingLeft)
                assertEquals(input, view.paddingRight)
                assertEquals(input, view.paddingStart)
            }
    ))

    // paddingStart
    add(ViewMapping.withAssertEquals(
            ARBITRARY_DIMENSIONS,
            android.R.attr.paddingStart,
            ViewProxy::setPaddingStart,
            BaseStyleBuilder<*, *>::paddingStart,
            BaseStyleBuilder<*, *>::paddingStartRes,
            { it.paddingStart }
    ))

    // paddingVertical
    add(ViewMapping.withCustomAssert(
            ARBITRARY_DIMENSIONS,
            android.R.attr.paddingVertical,
            ViewProxy::setPaddingVertical,
            BaseStyleBuilder<*, *>::paddingVertical,
            BaseStyleBuilder<*, *>::paddingVerticalRes,
            { view, input ->
                assertEquals(input, view.paddingBottom)
                assertEquals(input, view.paddingTop)
            }
    ))

    // contentDescription
    add(ViewMapping.withAssertEquals(
            ARBITRARY_STRINGS,
            android.R.attr.contentDescription,
            ViewProxy::setContentDescription,
            BaseStyleBuilder<*, *>::contentDescription,
            BaseStyleBuilder<*, *>::contentDescriptionRes,
            { it.contentDescription }
    ))

}
