package com.airbnb.paris.proxies

import android.content.Context
import android.view.ViewGroup
import android.view.ViewGroupStyleApplier
import android.widget.LinearLayout
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ViewGroupStyleApplierTest {

    private lateinit var context: Context
    private lateinit var viewGroup: ViewGroup
    private lateinit var applier: ViewGroupStyleApplier
    private lateinit var builder: ViewGroupStyleApplier.StyleBuilder

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        // An arbitrary ViewGroup
        viewGroup = LinearLayout(context)
        applier = ViewGroupStyleApplier(viewGroup)
        builder = ViewGroupStyleApplier.StyleBuilder()
    }

    @Test
    fun animateLayoutChanges() {
        viewGroup.layoutTransition = null
        applier.apply(builder.animateLayoutChanges(true).build())
        assertNotNull(viewGroup.layoutTransition)
    }

    @Test
    fun clipChildren() {
        viewGroup.clipChildren = false
        applier.apply(builder.clipChildren(true).build())
        assertTrue(viewGroup.clipChildren)
    }

    @Test
    fun clipToPadding() {
        viewGroup.clipToPadding = false
        applier.apply(builder.clipToPadding(true).build())
        assertTrue(viewGroup.clipToPadding)
    }
}
