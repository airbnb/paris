package com.airbnb.paris.sample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorInt
import android.support.annotation.Px
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Style
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.annotations.StyleableChild
import com.airbnb.paris.extensions.*

/**
 * A simple component with a title, content and divider.
 */
@Styleable("SectionView")
class SectionView : FrameLayout {

    @StyleableChild(R.styleable.SectionView_titleStyle)
    val titleView by lazy { findViewById<TextView>(R.id.title)!! }

    @StyleableChild(R.styleable.SectionView_contentStyle)
    val contentView by lazy { findViewById<TextView>(R.id.content)!! }

    private var dividerHeight: Int = 0
    private val dividerPaint = Paint()

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.view_section, this, true)
        // This call is necessary to support custom attributes (in this case `titleText`, `contentText`, etc) when this view is inflated from XML.
        style(attrs)
    }

    @Attr(R.styleable.SectionView_titleText)
    fun setTitleText(titleText: CharSequence) {
        titleView.text = titleText
    }

    @Attr(R.styleable.SectionView_contentText)
    fun setContentText(contentText: CharSequence) {
        contentView.text = contentText
    }

    @Attr(R.styleable.SectionView_dividerColor)
    fun setDividerColor(@ColorInt color: Int) {
        dividerPaint.color = color
        invalidate()
    }

    @Attr(R.styleable.SectionView_dividerHeight)
    fun setDividerHeight(@Px dividerHeight: Int) {
        this.dividerHeight = dividerHeight
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // A divider at the bottom of the view
        canvas.drawRect(
            paddingStart.toFloat(),
            (height - dividerHeight).toFloat(),
            (width - paddingEnd).toFloat(),
            height.toFloat(),
            dividerPaint
        )
    }

    companion object {

        private val DEFAULT_TITLE_STYLE = textViewStyle {
            add(TEXT_HEADLINE)
            backgroundRes(android.R.color.transparent)
            letterSpacing(0f)
            textAllCaps(false)
            textStyle(Typeface.NORMAL)
        }

        private val DEFAULT_CONTENT_STYLE = textViewStyle {
            add(TEXT_BODY)
            textAllCaps(false)
            textStyle(Typeface.NORMAL)
        }

        @Style
        val DEFAULT_STYLE = sectionViewStyle {
            backgroundRes(android.R.color.transparent)
            // Set each padding value independently so that they can be overridden in other styles.
            paddingBottomRes(R.dimen.space4)
            paddingEndRes(R.dimen.space4)
            paddingStartRes(R.dimen.space4)
            paddingTopRes(R.dimen.space4)
            titleStyle(DEFAULT_TITLE_STYLE)
            contentStyle(DEFAULT_CONTENT_STYLE)
            dividerColor(Color.parseColor("#DDDDDD"))
            dividerHeightDp(1)
        }

        @Style
        val BEACH_STYLE = sectionViewStyle {
            addDefault()
            paddingBottomRes(R.dimen.space8)
            paddingTopRes(R.dimen.space5)
            titleStyle {
                textColor(Color.parseColor("#FED255"))
                textStyle(Typeface.ITALIC)
            }
            contentStyle {
                textColor(Color.parseColor("#91617D"))
                textStyle(Typeface.ITALIC)
            }
            dividerColor(Color.parseColor("#FF8B85"))
            dividerHeightDp(12)
        }

        @Style
        val NEON_STYLE = sectionViewStyle {
            addDefault()
            background(ColorDrawable(Color.parseColor("#001f3f")))
            paddingBottomRes(R.dimen.space5)
            titleStyle {
                letterSpacing(.2f)
                textAllCaps(true)
                textColor(Color.YELLOW)
                textStyle(Typeface.BOLD)
            }
            contentStyle {
                textAllCaps(true)
                textColor(Color.parseColor("#F012BE"))
                textStyle(Typeface.BOLD)
            }
            dividerColor(Color.parseColor("#01FF70"))
            dividerHeightDp(4)
        }
    }
}
