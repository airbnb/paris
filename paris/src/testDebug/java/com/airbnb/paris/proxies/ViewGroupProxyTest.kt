package com.airbnb.paris.proxies

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ViewGroupProxyTest {

    private lateinit var context: Context
    private lateinit var viewGroup: ViewGroup
    private lateinit var proxy: ViewGroupProxy

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        // Use LinearLayout as an arbitrary ViewGroup
        viewGroup = LinearLayout(context)
        proxy = ViewGroupProxy(viewGroup)
    }

    @Test
    fun setAnimateLayoutChanges() {
        viewGroup.layoutTransition = null
        proxy.setAnimateLayoutChanges(true)
        assertNotNull(viewGroup.layoutTransition)
    }

    @Test
    fun setClipChildren() {
        viewGroup.clipChildren = false
        proxy.setClipChildren(true)
        assertTrue(viewGroup.clipChildren)
    }

    @Test
    fun setClipToPadding() {
        viewGroup.clipToPadding = false
        proxy.setClipToPadding(true)
        assertTrue(viewGroup.clipToPadding)
    }
}
