package com.airbnb.paris.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val marqueeView by lazy { findViewById<MarqueeView>(R.id.marquee) }
    private val section1View by lazy { findViewById<SectionView>(R.id.section1) }
    private val section2View by lazy { findViewById<SectionView>(R.id.section2) }
    private val section3View by lazy { findViewById<SectionView>(R.id.section3) }

    private var styleToggle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
         * Note that all the styling information has been completely separated from the activity
         * layout (except for style references). It all lives in styles which are explicitly
         * associated with specific custom view components, see:
         *
         * - MarqueeView.java
         *   - view_marquee.xml (layout)
         *   - styles_view_marquee.xml (styles)
         *
         * - SectionView.java
         *   - view_section.xml (layout)
         *   - styles_view_section.xml (styles)
         */

        setContentView(R.layout.activity_main)

        section1View.setButtonClickListener {
            Paris.style(marqueeView).run {
                if (styleToggle) applyDefault() else applyAlternate()
            }

            Paris.style(section1View).run {
                if (styleToggle) applyDefault() else applyAlternate()
            }

            Paris.style(section2View).run {
                if (styleToggle) applyInterstitial() else applyAlternateInterstitial()
            }

            Paris.style(section3View).run {
                if (styleToggle) applyDefault() else applyAlternate()
            }

            styleToggle = !styleToggle
        }
    }
}
