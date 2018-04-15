package com.airbnb.paris.sample;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Style;
import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.annotations.StyleableChild;

@Styleable("SectionView")
public class SectionView extends BaseView {

    @Style static final int DEFAULT_STYLE = R.style.SectionView;
    @Style static final int ALTERNATE_STYLE = R.style.SectionView_Alternate;
    @Style static final int INTERSTITIAL_STYLE = R.style.SectionView_Interstitial;
    @Style static final int ALTERNATE_INTERSTITIAL_STYLE = R.style.SectionView_Interstitial_Alternate;

    @StyleableChild(R.styleable.SectionView_titleStyle)
    TextView titleView;

    @StyleableChild(R.styleable.SectionView_contentStyle)
    TextView contentView;

    @StyleableChild(R.styleable.SectionView_buttonStyle)
    Button buttonView;

    public SectionView(Context context) {
        super(context);
    }

    public SectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(AttributeSet attrs) {
        titleView = findViewById(R.id.title);
        contentView = findViewById(R.id.content);
        buttonView = findViewById(R.id.button);
        Paris.style(this).apply(attrs);
    }

    @Override
    public int layout() {
        return R.layout.view_section;
    }

    @Attr(R.styleable.SectionView_titleText)
    public void setTitleText(CharSequence titleText) {
        titleView.setText(titleText);
    }

    @Attr(R.styleable.SectionView_contentText)
    public void setContentText(CharSequence contentText) {
        contentView.setText(contentText);
    }

    @Attr(R.styleable.SectionView_buttonText)
    public void setButtonText(@Nullable CharSequence buttonText) {
        buttonView.setText(buttonText);
        buttonView.setVisibility(buttonText != null ? View.VISIBLE : View.GONE);
    }

    public void setButtonClickListener(@Nullable OnClickListener listener) {
        buttonView.setOnClickListener(listener);
    }
}
