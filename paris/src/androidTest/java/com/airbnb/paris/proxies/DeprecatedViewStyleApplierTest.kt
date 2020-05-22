package com.airbnb.paris.proxies

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.view.ViewStyleApplier
import android.view.ViewStyleApplier.StyleBuilder
import android.widget.FrameLayout
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.airbnb.paris.test.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeprecatedViewStyleApplierTest {

    private lateinit var context: Context
    private lateinit var res: Resources
    private lateinit var view: View
    private lateinit var styleApplier: ViewStyleApplier
    private lateinit var styleBuilder: StyleBuilder

    private var params: ViewGroup.LayoutParams? = null
    private var marginParams: ViewGroup.MarginLayoutParams? = null

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        res = context.resources
        view = View(context)
        styleApplier = ViewStyleApplier(view)
        styleBuilder = StyleBuilder()
    }

    @Test
    fun auto() {
        for (mapping in VIEW_MAPPINGS) {
            mapping as BaseViewMapping<Any, *, View, Any>

            setup()

            mapping.testValues.forEach {
                // Set the value on the style builder
                mapping.setStyleBuilderValueFunction(styleBuilder, it)
                // Apply the style to the view
                styleApplier.apply(styleBuilder.build())
                // Check that the value was correctly applied
                mapping.assertViewSet(view, it)
            }
        }
    }

    @Test
    fun layoutParamsWidthHeight() {
        params = view.layoutParams
        assertNull(params)

        ViewStyleApplier(view).apply(R.style.Test_View_width_height)

        params = view.layoutParams
        assertNotNull(params)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_width), params!!.width)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_height), params!!.height)
    }

    @Test
    fun layoutParamsIgnoreWidthHeight() {
        params = view.layoutParams
        assertNull(params)

        ViewStyleApplier(view)
            .builder()
            .ignoreLayoutWidthAndHeight(true)
            .add(R.style.Test_View_width_height)
            .apply()

        params = view.layoutParams
        assertNull(params)
    }

    @Test
    fun layoutParamsMargin() {
        params = view.layoutParams
        assertNull(params)

        ViewStyleApplier(view).apply(R.style.Test_View_margin)

        marginParams = view.layoutParams as ViewGroup.MarginLayoutParams
        assertNotNull(marginParams)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_margin), marginParams!!.bottomMargin)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_margin), marginParams!!.leftMargin)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_margin), marginParams!!.rightMargin)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_margin), marginParams!!.topMargin)
    }

    @Test
    fun layoutParamsMargins() {
        params = view.layoutParams
        assertNull(params)

        ViewStyleApplier(view).apply(R.style.Test_View_margins)

        marginParams = view.layoutParams as ViewGroup.MarginLayoutParams
        assertNotNull(marginParams)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_marginBottom), marginParams!!.bottomMargin)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_marginLeft), marginParams!!.leftMargin)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_marginRight), marginParams!!.rightMargin)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_marginTop), marginParams!!.topMargin)
    }

    @Test
    fun viewBackground() {
        assertNull(view.background)
        ViewStyleApplier(view).apply(R.style.Test_View_background)
        assertNotNull(view.background)
    }

    @Test
    fun viewLayoutGravity() {
        ViewStyleApplier(view).apply(R.style.Test_View_layoutGravity)
        assertNull(view.layoutParams)
        view.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        ViewStyleApplier(view).apply(R.style.Test_View_layoutGravity)
        assertEquals(res.getInteger(R.integer.test_view_layout_gravity), (view.layoutParams as FrameLayout.LayoutParams).gravity)
    }

    @Test
    fun viewMinWidth() {
        assertEquals(0, view.minimumWidth)
        ViewStyleApplier(view).apply(R.style.Test_View_minWidth)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_min_width), view.minimumWidth)
    }

    @Test
    fun viewPadding() {
        assertEquals(0, view.paddingBottom)
        assertEquals(0, view.paddingLeft)
        assertEquals(0, view.paddingRight)
        assertEquals(0, view.paddingTop)
        ViewStyleApplier(view).apply(R.style.Test_View_padding)
        val padding = res.getDimensionPixelSize(R.dimen.test_view_padding);
        assertEquals(padding, view.paddingBottom)
        assertEquals(padding, view.paddingLeft)
        assertEquals(padding, view.paddingRight)
        assertEquals(padding, view.paddingTop)
    }

    @Test
    fun viewPaddings() {
        assertEquals(0, view.paddingBottom)
        assertEquals(0, view.paddingLeft)
        assertEquals(0, view.paddingRight)
        assertEquals(0, view.paddingTop)
        ViewStyleApplier(view).apply(R.style.Test_View_paddings)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_paddingBottom), view.paddingBottom)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_paddingLeft), view.paddingLeft)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_paddingRight), view.paddingRight)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_paddingTop), view.paddingTop)
    }

    @Test
    fun viewVisibility() {
        assertEquals(View.VISIBLE, view.visibility)
        ViewStyleApplier(view).apply(R.style.Test_View_visibility)
        assertEquals(View.GONE, view.visibility)
    }
}
