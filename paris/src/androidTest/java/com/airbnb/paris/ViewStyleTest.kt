package com.airbnb.paris

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewStyleTest {

    @Test
    @Throws(Exception::class)
    fun basic() {
        val c = InstrumentationRegistry.getTargetContext()
        val r = c.resources
        val view = View(c)

        assertEquals(null, view.background)

        Paris.change(view).apply(R.style.test)

        assertEquals(r.getDrawable(android.R.drawable.ic_delete), view.background)
    }
}
