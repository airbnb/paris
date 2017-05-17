package com.airbnb.parislibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.airbnb.paris.annotations.Styleable;

@Styleable("Hi")
public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
