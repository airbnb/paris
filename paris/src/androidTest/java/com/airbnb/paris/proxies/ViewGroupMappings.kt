package com.airbnb.paris.proxies

import android.*
import android.view.*
import junit.framework.*

internal class ViewGroupMapping<I> private constructor(
        testValues: List<I>,
        attrRes: Int,
        setProxyFunction: ViewGroupProxy.(I) -> Unit,
        setStyleBuilderValueFunction: ViewGroupProxyStyleApplier.BaseStyleBuilder<*, *>.(I) -> Any,
        setStyleBuilderResFunction: ViewGroupProxyStyleApplier.BaseStyleBuilder<*, *>.(Int) -> Any,
        /**
         * A function which, when called, will assert that the view has been successfully modified
         * by the associated proxy and/or style builder methods
         */
        assertViewSet: (ViewGroup, I) -> Unit) :
        BaseViewMapping<ViewGroupProxyStyleApplier.BaseStyleBuilder<*, *>, ViewGroupProxy, ViewGroup, I>(
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
                setProxyFunction: ViewGroupProxy.(I) -> Unit,
                setStyleBuilderValueFunction: ViewGroupProxyStyleApplier.BaseStyleBuilder<*, *>.(I) -> Any,
                setStyleBuilderResFunction: ViewGroupProxyStyleApplier.BaseStyleBuilder<*, *>.(Int) -> Any,
                assertViewSet: (ViewGroup, I) -> Unit): ViewGroupMapping<I> {
            return ViewGroupMapping(
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
                setProxyFunction: ViewGroupProxy.(I) -> Unit,
                setStyleBuilderValueFunction: ViewGroupProxyStyleApplier.BaseStyleBuilder<*, *>.(I) -> Any,
                setStyleBuilderResFunction: ViewGroupProxyStyleApplier.BaseStyleBuilder<*, *>.(Int) -> Any,
                getViewFunction: (ViewGroup) -> I): ViewGroupMapping<I> {
            return withCustomAssert(
                    testValues,
                    attrRes,
                    setProxyFunction,
                    setStyleBuilderValueFunction,
                    setStyleBuilderResFunction,
                    { View, input -> Assert.assertEquals(input, getViewFunction(View)) })
        }
    }
}

internal val VIEW_GROUP_MAPPINGS = ArrayList<ViewGroupMapping<*>>().apply {

    // clipChildren
    add(ViewGroupMapping.withAssertEquals(
            BOOLS,
            R.attr.clipChildren,
            ViewGroupProxy::setClipChildren,
            ViewGroupProxyStyleApplier.BaseStyleBuilder<*, *>::clipChildren,
            ViewGroupProxyStyleApplier.BaseStyleBuilder<*, *>::clipChildrenRes,
            { it.clipChildren }
    ))

    // clipToPadding
    add(ViewGroupMapping.withAssertEquals(
            BOOLS,
            R.attr.clipToPadding,
            ViewGroupProxy::setClipToPadding,
            ViewGroupProxyStyleApplier.BaseStyleBuilder<*, *>::clipToPadding,
            ViewGroupProxyStyleApplier.BaseStyleBuilder<*, *>::clipToPaddingRes,
            { it.clipToPadding }
    ))
}
