package com.airbnb.paris.test

import android.content.Context
import android.content.res.Resources
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.widget.FrameLayout
import com.airbnb.paris.proxies.ViewProxyStyleApplier
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewStyleTest {

    lateinit var context: Context
    lateinit var res: Resources
    lateinit var view: View

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        res = context.resources
        view = View(context)
    }

    @Test
    fun viewBackground() {
        assertNull(view.background)
        ViewProxyStyleApplier(view).apply(R.style.Test_View_background)
        assertNotNull(view.background)
    }

    @Test
    fun viewLayoutGravity() {
        ViewProxyStyleApplier(view).apply(R.style.Test_View_layoutGravity)
        assertNull(view.layoutParams)
        view.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        ViewProxyStyleApplier(view).apply(R.style.Test_View_layoutGravity)
        assertEquals(res.getInteger(R.integer.test_view_layout_gravity), (view.layoutParams as FrameLayout.LayoutParams).gravity)
    }

    @Test
    fun viewMinWidth() {
        assertEquals(0, view.minimumWidth)
        ViewProxyStyleApplier(view).apply(R.style.Test_View_minWidth)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_min_width), view.minimumWidth)
    }

    @Test
    fun viewPadding() {
        assertEquals(0, view.paddingBottom)
        assertEquals(0, view.paddingLeft)
        assertEquals(0, view.paddingRight)
        assertEquals(0, view.paddingTop)
        ViewProxyStyleApplier(view).apply(R.style.Test_View_padding)
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
        ViewProxyStyleApplier(view).apply(R.style.Test_View_paddings)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_paddingBottom), view.paddingBottom)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_paddingLeft), view.paddingLeft)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_paddingRight), view.paddingRight)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_view_paddingTop), view.paddingTop)
    }

    @Test
    fun viewVisibility() {
        assertEquals(View.VISIBLE, view.visibility)
        ViewProxyStyleApplier(view).apply(R.style.Test_View_visibility)
        assertEquals(View.GONE, view.visibility)
    }
}
