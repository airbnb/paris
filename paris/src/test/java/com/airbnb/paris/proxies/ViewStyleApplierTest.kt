package com.airbnb.paris.proxies

import android.content.Context
import android.view.View
import android.view.ViewStyleApplier
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ViewStyleApplierTest {

    private lateinit var context: Context
    private lateinit var view: View
    private lateinit var applier: ViewStyleApplier
    private lateinit var builder: ViewStyleApplier.StyleBuilder

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        view = View(context)
        applier = ViewStyleApplier(view)
        builder = ViewStyleApplier.StyleBuilder()
    }

    @Test
    fun visibility_visible() {
        // Since visible is the default first set the visibility to something else
        view.visibility = View.GONE
        applier.apply(builder.visibility(View.VISIBLE).build())
        assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun visibility_invisible() {
        applier.apply(builder.visibility(View.INVISIBLE).build())
        assertEquals(View.INVISIBLE, view.visibility)
    }

    @Test
    fun visibility_gone() {
        applier.apply(builder.visibility(View.GONE).build())
        assertEquals(View.GONE, view.visibility)
    }
}
