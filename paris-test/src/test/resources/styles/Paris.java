package com.airbnb.paris;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.airbnb.paris.test.MyView;
import com.airbnb.paris.test.MyViewStyleApplier;
import com.airbnb.paris.test.R;

public final class Paris extends ParisBase {
    public static ViewStyleApplier style(View view) {
        return process(new ViewStyleApplier(view));
    }

    public static TextViewStyleApplier style(TextView view) {
        return process(new TextViewStyleApplier(view));
    }

    public static MyViewStyleApplier style(MyView view) {
        return process(new MyViewStyleApplier(view));
    }

    /**
     * For debugging */
    public static void assertStylesContainSameAttributes(Context context) {
        MyView MyView = new MyView(context);
        StyleApplierUtils.Companion.assertSameAttributes(style(MyView), new Style(R.style.MyView_Red), new Style(R.style.MyView_Green), new Style(R.style.MyView_Blue));
    }
}