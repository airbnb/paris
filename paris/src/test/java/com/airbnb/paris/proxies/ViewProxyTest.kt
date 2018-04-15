package com.airbnb.paris.proxies

import android.content.Context
import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ViewProxyTest {

    private lateinit var context: Context
    private lateinit var view: View
    private lateinit var proxy: ViewProxy

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        view = View(context)
        proxy = ViewProxy(view)
    }

    @Test
    fun setVisibility_visibleProgrammatic() {
        view.visibility = View.GONE
        proxy.setVisibility(View.VISIBLE)
        assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun setVisibility_visibleXml() {
        view.visibility = View.GONE
        // When set in XML the index corresponding to visible is 0
        proxy.setVisibility(0)
        assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun setVisibility_invisibleProgrammatic() {
        proxy.setVisibility(View.INVISIBLE)
        assertEquals(View.INVISIBLE, view.visibility)
    }

    @Test
    fun setVisibility_invisibleXml() {
        // When set in XML the index corresponding to invisible is 1
        proxy.setVisibility(1)
        assertEquals(View.INVISIBLE, view.visibility)
    }

    @Test
    fun setVisibility_goneProgrammatic() {
        proxy.setVisibility(View.GONE)
        assertEquals(View.GONE, view.visibility)
    }

    @Test
    fun setVisibility_goneXml() {
        // When set in XML the index corresponding to gone is 2
        proxy.setVisibility(2)
        assertEquals(View.GONE, view.visibility)
    }
}
