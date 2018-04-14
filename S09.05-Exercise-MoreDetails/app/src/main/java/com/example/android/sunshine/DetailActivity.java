/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

import java.net.URI;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
//      k (21) Implement LoaderManager.LoaderCallbacks<Cursor>

    /*
     * In this Activity, you can share the selected day's forecast. No social sharing is complete
     * without using a hashtag. #BeTogetherNotTheSame
     */
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    public static final String[] weatherDetails = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

//  k (18) Create a String array containing the names of the desired data columns from our ContentProvider
//  k (19) Create constant int values representing each column name's position above
    public static final int weatherDateIndex = 0;
    public static final int weatherMaxTempIndex = 1;
    public static final int weatherMinTempIndex = 2;
    public static final int weatherPressureIndex = 3;
    public static final int weatherWindIndex = 4;
    public static final int weatherDegIndex = 5;
    public static final int weatherHUmidityIndex = 6;
    public static final int weatherIdIndex = 7;
//  k (20) Create a constant int to identify our loader used in DetailActivity
    int idLoader = 89;

    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private String mForecastSummary;

//  k (15) Declare a private Uri field called mUri
    Uri mUri;

//  k (10) Remove the mWeatherDisplay TextView declaration
    private TextView date;
    private TextView description;
    private TextView high;
    private TextView low;
    private TextView humidity;
    private TextView wind;
    private TextView pressure;

//  k (11) Declare TextViews for the date, description, high, low, humidity, wind, and pressure

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//      k (12) Remove mWeatherDisplay TextView
        date = (TextView) findViewById(R.id.todays_date);
        description = (TextView) findViewById(R.id.description);
        high = (TextView) findViewById(R.id.hi_temp);
        low = (TextView) findViewById(R.id.lo_temp);
        humidity = (TextView) findViewById(R.id.humidity);
        wind = (TextView) findViewById(R.id.wind);
        pressure = (TextView) findViewById(R.id.pressure);
//      k (13) Find each of the TextViews by ID

//      k (14) Remove the code that checks for extra text
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            mUri = intentThatStartedThisActivity.getData();
            if(mUri == null) {
                throw new NullPointerException("Whoa it's null!");
            }
        }
//      k (16) Use getData to get a reference to the URI passed with this Activity's Intent
//      k (17) Throw a NullPointerException if that URI is null
//      k (35) Initialize the loader for DetailActivity
        getSupportLoaderManager().initLoader(idLoader, null, this);
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     *
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Settings menu item clicked */
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == idLoader) {
            return new CursorLoader(this, mUri, weatherDetails, null, null, null);
        } else {
            throw new RuntimeException("Whoa no way!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean isValid = data != null && data.moveToFirst();
        if(!isValid) {
            return;
        }
        long localdate = data.getLong(weatherDateIndex);
        String dateText = SunshineDateUtils.getFriendlyDateString(this, localdate, true);
        date.setText(dateText);
        int localDes = data.getInt(weatherIdIndex);
        String descriptionPretty = SunshineWeatherUtils.getStringForWeatherCondition(this, localDes);
        description.setText(descriptionPretty);
        double localHi = data.getDouble(weatherMaxTempIndex);
        String hiString = SunshineWeatherUtils.formatTemperature(this, localHi);
        high.setText(hiString);
        double locallo = data.getDouble(weatherMinTempIndex);
        String loString = SunshineWeatherUtils.formatTemperature(this, locallo);
        low.setText(loString);
        float windNum = data.getFloat(weatherWindIndex);
        float windDir = data.getFloat(weatherDegIndex);
        String windString = SunshineWeatherUtils.getFormattedWind(this, windNum, windDir);
        wind.setText(windString);
        float humidityNum = data.getFloat(weatherHUmidityIndex);
        String humidityString = getString(R.string.format_humidity, humidityNum);
        humidity.setText(humidityString);
        float pressureNum = data.getFloat(weatherPressureIndex);
        String pressureString = getString(R.string.format_pressure, pressureNum);
        pressure.setText(pressureString);
        mForecastSummary = dateText + localDes + localHi + locallo + windString + humidityString + pressureString;

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

//  k (22) Override onCreateLoader
//          k (23) If the loader requested is our detail loader, return the appropriate CursorLoader

//  k (24) Override onLoadFinished
//      k (25) Check before doing anything that the Cursor has valid data
//      k (26) Display a readable data string
//      k (27) Display the weather description (using SunshineWeatherUtils)
//      k (28) Display the high temperature
//      k (29) Display the low temperature
//      k (30) Display the humidity
//      l (31) Display the wind speed and direction
//      k (32) Display the pressure
//      k (33) Store a forecast summary in mForecastSummary


//  k (34) Override onLoaderReset, but don't do anything in it yet

}