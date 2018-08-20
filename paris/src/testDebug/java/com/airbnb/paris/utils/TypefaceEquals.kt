package com.airbnb.paris.utils

import android.graphics.Typeface
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.robolectric.Shadows.shadowOf

/**
 * ShadowTypeface should be used for Typeface comparison; otherwise false positives will be reported.
 */
fun assertTypefaceEquals(expected: Typeface?, actual: Typeface?) {
    if (expected == null) {
        assertNull(actual)
        return
    }

    val expectedShadow = shadowOf(expected)
    val actualShadow = shadowOf(actual)

    // Two checks since FontDesc doesn't implement toString() and AssertionError message is illegible
    assertEquals(expectedShadow.fontDescription.familyName, actualShadow.fontDescription.familyName)
    assertEquals(expectedShadow.fontDescription.style, actualShadow.fontDescription.style)
}
