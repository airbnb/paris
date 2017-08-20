package com.airbnb.paris;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.airbnb.paris.proxy.TextViewProxyStyleApplier;
import com.airbnb.paris.proxy.ViewProxyStyleApplier;
import com.airbnb.paris.test.MyView;
import com.airbnb.paris.test.MyViewStyleApplier;

public final class Paris extends ParisBase {
    public static ViewProxyStyleApplier style(View view) {
        return process(new ViewProxyStyleApplier(view));
    }

    public static TextViewProxyStyleApplier style(TextView view) {
        return process(new TextViewProxyStyleApplier(view));
    }

    public static MyViewStyleApplier style(MyView view) {
        return process(new MyViewStyleApplier(view));
    }

    /**
     * For debugging */
    public static void assertStylesContainSameAttributes(Context context) {
    }
}