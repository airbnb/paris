package com.airbnb.paris.proxies

import android.R
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.TextUtils
import android.view.Gravity
import android.widget.TextView
import android.widget.TextViewStyleApplier.StyleBuilder
import com.airbnb.paris.proxies.TextViewMapping.Companion.withAssertEquals
import com.airbnb.paris.proxies.TextViewMapping.Companion.withCustomAssert
import junit.framework.Assert.assertEquals
import org.junit.Assert

internal class TextViewMapping<I : Any> private constructor(
    testValues: List<I>,
    attrRes: Int,
    setProxyFunction: TextViewProxy.(I) -> Unit,
    setStyleBuilderValueFunction: StyleBuilder.(I) -> Any,
    setStyleBuilderResFunction: StyleBuilder.(Int) -> Any,
    /**
     * A function which, when called, will assert that the view has been successfully modified
     * by the associated proxy and/or style builder methods
     */
    assertViewSet: (TextView, I) -> Unit
) :
    BaseViewMapping<StyleBuilder, TextViewProxy, TextView, I>(
        testValues,
        attrRes,
        setProxyFunction,
        setStyleBuilderValueFunction,
        setStyleBuilderResFunction,
        assertViewSet
    ) {

    companion object {

        fun <I : Any> withCustomAssert(
            testValues: List<I>,
            attrRes: Int,
            setProxyFunction: TextViewProxy.(I) -> Unit,
            setStyleBuilderValueFunction: StyleBuilder.(I) -> Any,
            setStyleBuilderResFunction: StyleBuilder.(Int) -> Any,
            assertViewSet: (TextView, I) -> Unit
        ): TextViewMapping<I> {
            return TextViewMapping(
                testValues,
                attrRes,
                setProxyFunction,
                setStyleBuilderValueFunction,
                setStyleBuilderResFunction,
                assertViewSet
            )
        }

        fun <I : Any> withAssertEquals(
            testValues: List<I>,
            attrRes: Int,
            setProxyFunction: TextViewProxy.(I) -> Unit,
            setStyleBuilderValueFunction: StyleBuilder.(I) -> Any,
            setStyleBuilderResFunction: StyleBuilder.(Int) -> Any,
            getViewFunction: (TextView) -> I
        ): TextViewMapping<I> {
            return withCustomAssert(
                testValues,
                attrRes,
                setProxyFunction,
                setStyleBuilderValueFunction,
                setStyleBuilderResFunction,
                { textView, input -> assertEquals(input, getViewFunction(textView)) })
        }
    }
}

internal val TEXT_VIEW_MAPPINGS = ArrayList<TextViewMapping<*>>().apply {

    // drawableBottom
    add(withAssertEquals(
        listOf(ColorDrawable(Color.GREEN)),
        android.R.attr.drawableBottom,
        TextViewProxy::setDrawableBottom,
        StyleBuilder::drawableBottom,
        StyleBuilder::drawableBottomRes,
        { it.compoundDrawables[3] }
    ))

    // drawableLeft
    add(withAssertEquals(
        listOf(ColorDrawable(Color.GREEN)),
        android.R.attr.drawableLeft,
        TextViewProxy::setDrawableLeft,
        StyleBuilder::drawableLeft,
        StyleBuilder::drawableLeftRes,
        { it.compoundDrawables[0] }
    ))

    // drawableRight
    add(withAssertEquals(
        listOf(ColorDrawable(Color.GREEN)),
        android.R.attr.drawableRight,
        TextViewProxy::setDrawableRight,
        StyleBuilder::drawableRight,
        StyleBuilder::drawableRightRes,
        { it.compoundDrawables[2] }
    ))

    // drawableTop
    add(withAssertEquals(
        listOf(ColorDrawable(Color.GREEN)),
        android.R.attr.drawableTop,
        TextViewProxy::setDrawableTop,
        StyleBuilder::drawableTop,
        StyleBuilder::drawableTopRes,
        { it.compoundDrawables[1] }
    ))

    // ellipsize
    add(withAssertEquals(
        (1..4).toList(),
        android.R.attr.ellipsize,
        TextViewProxy::setEllipsize,
        StyleBuilder::ellipsize,
        StyleBuilder::ellipsizeRes,
        {
            mapOf(
                TextUtils.TruncateAt.START to 1,
                TextUtils.TruncateAt.MIDDLE to 2,
                TextUtils.TruncateAt.END to 3,
                TextUtils.TruncateAt.MARQUEE to 4
            )[it.ellipsize]!!
        }
    ))

    // gravity
    add(withCustomAssert(
        listOf(
            Gravity.BOTTOM,
            Gravity.CENTER,
            Gravity.CENTER_VERTICAL,
            Gravity.START,
            Gravity.TOP
        ),
        android.R.attr.gravity,
        TextViewProxy::setGravity,
        StyleBuilder::gravity,
        StyleBuilder::gravityRes,
        { view, input ->
            assertEquals(input, view.gravity and input)
        }
    ))

    // letterSpacing
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        add(withAssertEquals(
            ARBITRARY_FLOATS,
            android.R.attr.letterSpacing,
            TextViewProxy::setLetterSpacing,
            StyleBuilder::letterSpacing,
            StyleBuilder::letterSpacingRes,
            { it.letterSpacing }
        ))
    }

    // lines
    add(withCustomAssert(
        ARBITRARY_INTS,
        R.attr.lines,
        TextViewProxy::setLines,
        StyleBuilder::lines,
        StyleBuilder::linesRes,
        { view, input ->
            assertEquals(input, view.minLines)
            assertEquals(input, view.maxLines)
        }
    ))

    // lineSpacingExtra
    add(withAssertEquals(
        ARBITRARY_INTS,
        R.attr.lineSpacingExtra,
        TextViewProxy::setLineSpacingExtra,
        StyleBuilder::lineSpacingExtra,
        StyleBuilder::lineSpacingExtraRes,
        { it.lineSpacingExtra.toInt() }
    ))

    // lineSpacingMultiplier
    add(withAssertEquals(
        ARBITRARY_FLOATS,
        R.attr.lineSpacingMultiplier,
        TextViewProxy::setLineSpacingMultiplier,
        StyleBuilder::lineSpacingMultiplier,
        StyleBuilder::lineSpacingMultiplierRes,
        { it.lineSpacingMultiplier }
    ))

    // maxLines
    add(withAssertEquals(
        ARBITRARY_INTS,
        R.attr.maxLines,
        TextViewProxy::setMaxLines,
        StyleBuilder::maxLines,
        StyleBuilder::maxLinesRes,
        { it.maxLines }
    ))

    // minLines
    add(withAssertEquals(
        ARBITRARY_INTS,
        R.attr.minLines,
        TextViewProxy::setMinLines,
        StyleBuilder::minLines,
        StyleBuilder::minLinesRes,
        { it.minLines }
    ))

    // minWidth
    add(withAssertEquals(
        ARBITRARY_INTS,
        R.attr.minWidth,
        TextViewProxy::setMinWidth,
        StyleBuilder::minWidth,
        StyleBuilder::minWidthRes,
        { it.minWidth }
    ))

    // singleLine
    add(withCustomAssert(
        listOf(true),
        R.attr.singleLine,
        TextViewProxy::setSingleLine,
        StyleBuilder::singleLine,
        StyleBuilder::singleLineRes,
        { view, _ ->
            Assert.assertEquals(1, view.minLines)
            Assert.assertEquals(1, view.maxLines)
        }
    ))

    // textColor
    add(withAssertEquals(
        ARBITRARY_COLOR_STATE_LISTS,
        R.attr.textColor,
        TextViewProxy::setTextColor,
        StyleBuilder::textColor,
        StyleBuilder::textColorRes,
        { it.textColors }
    ))

    // textColorHint
    add(withAssertEquals(
        ARBITRARY_COLOR_STATE_LISTS,
        R.attr.textColorHint,
        TextViewProxy::setTextColorHint,
        StyleBuilder::textColorHint,
        StyleBuilder::textColorHintRes,
        { it.hintTextColors }
    ))

    // textSize
    add(withAssertEquals(
        ARBITRARY_INTS.filter { it >= 0 },
        R.attr.textSize,
        TextViewProxy::setTextSize,
        StyleBuilder::textSize,
        StyleBuilder::textSizeRes,
        { it.textSize.toInt() }
    ))

    // text
    add(withAssertEquals(
        ARBITRARY_STRINGS,
        R.attr.text,
        TextViewProxy::setText,
        StyleBuilder::text,
        StyleBuilder::textRes,
        { it.text }
    ))
}
