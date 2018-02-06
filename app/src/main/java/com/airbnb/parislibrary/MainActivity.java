package com.airbnb.parislibrary;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.spanned_text);

        Paris.spannableBuilder()
                .append("The following text is rendered using a single TextView and the spannable Apis:\n")
                .append("This line has no style\n")
                .append("This line is styled using R.style.Blue\n", R.style.Blue)
                .append("This line is styled using R.style.Green\n", R.style.Green)
                .append("This line is using a system style\n", android.R.style.TextAppearance_Holo_Large)
                .append("This line is using a style created programmatically\n", Paris.styleBuilder(textView)
                        .textColor(Color.RED)
                        .textSize(30)
                        .build())
                .append("This line is styled using android:textAppearance", R.style.Purple)
                .applyTo(textView);
    }
}
