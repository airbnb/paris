package com.airbnb.paris;

import android.view.View;
import android.widget.TextView;
import com.airbnb.paris.test.MyView;
import com.airbnb.paris.test.MyViewStyleApplier;

public final class Paris {

    public static ViewStyleApplier change(View view) {
        return new ViewStyleApplier(view);
    }

    public static TextViewStyleApplier change(TextView view) {
        return new TextViewStyleApplier(view);
    }

    public static MyViewStyleApplier change(MyView view) {
        return new MyViewStyleApplier(view);
    }
}
