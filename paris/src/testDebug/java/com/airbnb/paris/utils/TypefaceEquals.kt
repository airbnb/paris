package com.airbnb.paris.utils

import android.graphics.Typeface
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.robolectric.Shadows.shadowOf

/**
 * ShadowTypeface should be used for Typeface comparison; otherwise false positives will be reported.
 *
 * We can't rely on Typeface class 'equals' method since it's comparing native implementations which
 * are not available for Robolectric. Obtaining shadow objects
 * (<a href="http://robolectric.org/extending/">http://robolectric.org/extending/</a>)
 * of provided Typeface instances enables us to simply check if font family name and style index
 * are equal.
 *
 * @see org.robolectric.shadows.ShadowTypeface
 */
fun assertTypefaceEquals(expected: Typeface?, actual: Typeface?) {
    if (expected == null) {
        assertNull(actual)
        return
    }

    // Find ShadowTypeface equivalents of provided Typeface objects
    val expectedShadow = shadowOf(expected)
    val actualShadow = shadowOf(actual)

    // Two checks since FontDesc doesn't implement toString() and AssertionError message is illegible
    assertEquals(expectedShadow.fontDescription.familyName, actualShadow.fontDescription.familyName)
    assertEquals(expectedShadow.fontDescription.style, actualShadow.fontDescription.style)
}
