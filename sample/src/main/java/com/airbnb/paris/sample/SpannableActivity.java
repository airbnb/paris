package com.airbnb.paris.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.airbnb.paris.Paris;

import androidx.appcompat.app.AppCompatActivity;

public class SpannableActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spannable);

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
