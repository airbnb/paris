package com.airbnb.paris

import android.content.Context
import android.content.res.Resources
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewStyleTest {

    var context: Context? = null
    var res: Resources? = null

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        res = context!!.resources
    }

    @Test
    @Throws(Exception::class)
    fun viewBackground() {
        val view = View(context)
        assertEquals(null, view.background)
        Styles.change(view).apply(R.style.Test_View_background)
        assertNotEquals(null, view.background)
    }
}
