package com.airbnb.paris.sample;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Style;
import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.annotations.StyleableChild;

@Styleable("MarqueeView")
public class MarqueeView extends BaseView {

    @Style static final int DEFAULT_STYLE = R.style.MarqueeView;
    @Style static final int ALTERNATE_STYLE = R.style.MarqueeView_Alternate;

    ImageView imageView;

    @StyleableChild(R.styleable.MarqueeView_displayStyle)
    TextView displayView;

    public MarqueeView(Context context) {
        super(context);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(AttributeSet attrs) {
        imageView = findViewById(R.id.image);
        displayView = findViewById(R.id.display);
        Paris.style(this).apply(attrs);
    }

    @Override
    public int layout() {
        return R.layout.view_marquee;
    }

    @Attr(R.styleable.MarqueeView_image)
    public void setImage(@DrawableRes int imageRes) {
        imageView.setImageResource(imageRes);
    }

    @Attr(R.styleable.MarqueeView_displayText)
    public void setDisplayText(CharSequence displayText) {
        displayView.setText(displayText);
    }
}
