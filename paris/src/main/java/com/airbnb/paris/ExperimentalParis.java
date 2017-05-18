package com.airbnb.paris;

import android.view.View;
import android.widget.TextView;

public class ExperimentalParis {

    public static ViewStyleApplier change(View view) {
        return new ViewStyleApplier(view);
    }

    public static TextViewStyleApplier change(TextView view) {
        return new TextViewStyleApplier(view);
    }
}
