package com.airbnb.paris.proxies

import android.widget.*
import android.widget.ImageView.*

internal class ImageViewMapping<I : Any> private constructor(
        testValues: List<I>,
        attrRes: Int,
        setProxyFunction: ImageViewProxy.(I) -> Unit,
        setStyleBuilderValueFunction: ImageViewProxyStyleApplier.StyleBuilder.(I) -> Any,
        setStyleBuilderResFunction: ImageViewProxyStyleApplier.StyleBuilder.(Int) -> Any,
        /**
         * A function which, when called, will assert that the view has been successfully modified
         * by the associated proxy and/or style builder methods
         */
        assertViewSet: (ImageView, I) -> Unit) :
        BaseViewMapping<ImageViewProxyStyleApplier.StyleBuilder, ImageViewProxy, ImageView, I>(
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
                setProxyFunction: ImageViewProxy.(I) -> Unit,
                setStyleBuilderValueFunction: ImageViewProxyStyleApplier.StyleBuilder.(I) -> Any,
                setStyleBuilderResFunction: ImageViewProxyStyleApplier.StyleBuilder.(Int) -> Any,
                assertViewSet: (ImageView, I) -> Unit): ImageViewMapping<I> {
            return ImageViewMapping(
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
                setProxyFunction: ImageViewProxy.(I) -> Unit,
                setStyleBuilderValueFunction: ImageViewProxyStyleApplier.StyleBuilder.(I) -> Any,
                setStyleBuilderResFunction: ImageViewProxyStyleApplier.StyleBuilder.(Int) -> Any,
                getViewFunction: (ImageView) -> I): ImageViewMapping<I> {
            return withCustomAssert(
                    testValues,
                    attrRes,
                    setProxyFunction,
                    setStyleBuilderValueFunction,
                    setStyleBuilderResFunction,
                    { ImageView, input -> junit.framework.Assert.assertEquals(input, getViewFunction(ImageView)) })
        }
    }
}

internal val IMAGE_VIEW_MAPPINGS = ArrayList<ImageViewMapping<*>>().apply {

    // scaleType
    add(ImageViewMapping.withAssertEquals(
            (0 until ScaleType.values().size).toList(),
            android.R.attr.scaleType,
            ImageViewProxy::setScaleType,
            ImageViewProxyStyleApplier.StyleBuilder::scaleType,
            ImageViewProxyStyleApplier.StyleBuilder::scaleTypeRes,
            {
                listOf(
                        ScaleType.MATRIX,
                        ScaleType.FIT_XY,
                        ScaleType.FIT_START,
                        ScaleType.FIT_CENTER,
                        ScaleType.FIT_END,
                        ScaleType.CENTER,
                        ScaleType.CENTER_CROP,
                        ScaleType.CENTER_INSIDE
                ).indexOf(it.scaleType)
            }
    ))

    // tint
    add(ImageViewMapping.withAssertEquals(
            ARBITRARY_COLOR_STATE_LISTS,
            android.R.attr.tint,
            ImageViewProxy::setTint,
            ImageViewProxyStyleApplier.StyleBuilder::tint,
            ImageViewProxyStyleApplier.StyleBuilder::tintRes,
            { it.imageTintList }
    ))
}
