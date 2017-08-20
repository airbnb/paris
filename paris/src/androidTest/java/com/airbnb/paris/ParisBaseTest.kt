package com.airbnb.paris

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParisBaseTest {

    private lateinit var context: Context
    private lateinit var view: View

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        view = View(context)
    }

    @Test
    fun getLastStyleApplied_none() {
        assertNull(ParisBase.getLastStyleApplied(view))
    }

    @Test
    fun getLastStyleApplied() {
        val style = Style(666)
        ParisBase.setLastStyleApplied(view, style)
        assertEquals(style, ParisBase.getLastStyleApplied(view))
    }
}
