package com.airbnb.paris;

import android.view.View;
import android.widget.TextView;
import com.airbnb.paris.test.MyView;
import com.airbnb.paris.test.MyViewStyleApplier;

public final class Paris {
    public static ViewStyleApplier style(View view) {
        return new ViewStyleApplier(view);
    }

    public static TextViewStyleApplier style(TextView view) {
        return new TextViewStyleApplier(view);
    }

    public static MyViewStyleApplier style(MyView view) {
        return new MyViewStyleApplier(view);
    }
}