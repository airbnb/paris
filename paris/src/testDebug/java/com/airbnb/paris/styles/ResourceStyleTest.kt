package com.airbnb.paris.styles

import android.content.Context
import com.airbnb.paris.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ResourceStyleTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
    }

    @Test
    fun equals() {
        assertEquals(ResourceStyle(R.style.Red), ResourceStyle(R.style.Red))
        assertNotEquals(ResourceStyle(R.style.Red), ResourceStyle(R.style.Green))
    }

    /**
     * If the style doesn't contain a given attribute but the theme does, we should default to it.
     */
    @Test
    fun styleableAttributeFromTheme() {
        context.setTheme(R.style.Theme_AppCompat)
        val emptyStyle = ResourceStyle(0)
        val ta = emptyStyle.obtainStyledAttributes(context, R.styleable.Paris_TextView)
        val actualTextAppearance = ta.getResourceId(R.styleable.Paris_TextView_android_textAppearance)
        assertEquals(android.R.style.TextAppearance_Material, actualTextAppearance)
    }
}
