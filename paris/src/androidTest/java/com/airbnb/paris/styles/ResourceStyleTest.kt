package com.airbnb.paris.styles

import android.support.test.runner.AndroidJUnit4
import com.airbnb.paris.test.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourceStyleTest {

    @Test
    fun equals() {
        assertEquals(ResourceStyle(R.style.Red), ResourceStyle(R.style.Red))
        assertNotEquals(ResourceStyle(R.style.Red), ResourceStyle(R.style.Green))
    }
}
