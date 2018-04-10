package com.example.android.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    TextView weatherDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // k (2) Display the weather forecast that was passed from MainActivity
        weatherDisplay = (TextView) findViewById(R.id.tv_display);
        Intent startIntent = getIntent();
        if(startIntent.hasExtra("weatherForDay")) {
            String weatherData = startIntent.getStringExtra("weatherForDay");
            weatherDisplay.setText(weatherData);
        }


    }
}