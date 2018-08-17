package com.airbnb.paris.sample

import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.StyleRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.airbnb.paris.extensions.dividerHeight
import com.airbnb.paris.extensions.style
import com.airbnb.paris.styles.Style

/**
 * This simple activity displays a few sections of content and changes the style of each section whenever the user clicks them. This is meant to
 * showcase some of the restyling capabilities of Paris. The actual meat of the style setup lives in [SectionView].
 */
class MainActivity : AppCompatActivity() {

    private val section1 by lazy { findViewById<SectionView>(R.id.section1) }
    private val section2 by lazy { findViewById<SectionView>(R.id.section2) }
    private val section3 by lazy { findViewById<SectionView>(R.id.section3) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val styles = listOf(
            SectionView.DEFAULT_STYLE,
            SectionView.BEACH_STYLE,
            SectionView.NEON_STYLE
        )

        // An infinite sequence that cycles through our list of styles.
        val styleSequence = generateSequence(styles[0]) {
            styles[(styles.indexOf(it) + 1) % styles.size]
        }

        section1.tag = styleSequence
        section2.tag = styleSequence
        section3.tag = styleSequence

        // Gets the next style in the sequence for a given View. Assumes the sequence has been set as the tag.
        @StyleRes
        fun nextStyle(view: SectionView): Style {
            @Suppress("UNCHECKED_CAST")
            val sequence = view.tag as Sequence<Style>
            view.tag = sequence.drop(1)
            return sequence.first()
        }

        val sectionClickListener = View.OnClickListener {
            // This cast is necessary for the correct style function to be called.
            it as SectionView
            it.style(nextStyle(it))
        }

        section1.setOnClickListener(sectionClickListener)
        section2.setOnClickListener(sectionClickListener)
        section3.setOnClickListener { view ->
            // This cast is necessary for the correct style function to be called.
            view as SectionView
            // This builds a new style based on the next one in the sequence, but with a small tweak.
            view.style {
                add(nextStyle(view))
                // Never show a divider on the last view.
                dividerHeight(0)
            }
        }

        // Simple hack to set the first style
        listOf(section1, section2, section3).forEach {
            it.performClick()
        }

        assertStylesContainSameAttributesAsync()
    }

    /**
     * This check makes it much safer to change view styles multiple times, notably when recycling views.
     * See https://github.com/airbnb/paris/wiki/View-Recycling#same-attributes-assertion for more information.
     */
    private fun assertStylesContainSameAttributesAsync() {
        if (BuildConfig.DEBUG) {
            // The check can run asynchronously to minimize impact
            AsyncTask.THREAD_POOL_EXECUTOR.execute {
                // Will throw in case of failure
                Paris.assertStylesContainSameAttributes(this)
            }
        }
    }
}
