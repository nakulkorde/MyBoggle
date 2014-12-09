package com.hoiwanlouis.myboggle;

/*
    Copyright (c) 2014  Hoi Wan Louis

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class WelcomeActivity extends Activity {

    private final String DEBUG_TAG = this.getClass().getSimpleName();

    // Keys for reading grid data from SharedPreferences
    private String BOGGLE_SHARED_PREFERENCES_FILE;
    private String DEFAULT_GRID_SIZE;
    private String BOGGLE_GRID_SIZE_KEY;
    private String BOGGLE_HIGH_SCORE_KEY;

    private SharedPreferences boggleSharedPreferences;
    private String boggleGridSize;

    // trigger switch to denote a grid change
    private boolean preferencesChanged = true;

    private StringBuilder sbToastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(DEBUG_TAG,"in onCreate()");
        super.onCreate(savedInstanceState);

        sbToastMessage = new StringBuilder();

        // init vars from strings.xml resources
        initStringResources();

        // connect to boggle shared preferences for use by BoardActivity
        connectSharedPreferences();

        // set default values in the apps SharedPreferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // register listener for SharedPreferences changes
        PreferenceManager.getDefaultSharedPreferences(this).
                registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        setContentView(R.layout.activity_welcome);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(DEBUG_TAG,"in onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(DEBUG_TAG,"in onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        boolean isItemSelected = false;

        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
        isItemSelected = super.onOptionsItemSelected(item);

        return isItemSelected;
    }

    public void goToGameSetup(View v) {
        Log.v(DEBUG_TAG,"in goToGameSetup");

        Intent intent = new Intent(this, BoardActivity.class);
        startActivity(intent);

    }

    //
    public void initStringResources() {
        Log.v(DEBUG_TAG,"initStringResources()");

        BOGGLE_SHARED_PREFERENCES_FILE = getString(R.string.boggle_shared_preferences_file);
        DEFAULT_GRID_SIZE = getString(R.string.default_size_of_boggle_grid);
        BOGGLE_GRID_SIZE_KEY = getString(R.string.boggle_grid_size_key);
        BOGGLE_HIGH_SCORE_KEY = getString(R.string.boggle_high_score_key);

        Log.i(DEBUG_TAG,"BOGGLE_SHARED_PREFERENCES_FILE:" + BOGGLE_SHARED_PREFERENCES_FILE);
        Log.i(DEBUG_TAG,"DEFAULT_GRID_SIZE:" + DEFAULT_GRID_SIZE);
        Log.i(DEBUG_TAG,"BOGGLE_GRID_SIZE_KEY:" + BOGGLE_GRID_SIZE_KEY);
        Log.i(DEBUG_TAG,"BOGGLE_HIGH_SCORE_KEY:" + BOGGLE_HIGH_SCORE_KEY);

    }

    //
    private void connectSharedPreferences() {
        Log.v(DEBUG_TAG,"connectSharedPreferences()");
        boggleSharedPreferences = getSharedPreferences(BOGGLE_SHARED_PREFERENCES_FILE, 0);
    }


    // retrieve the grid size from shared preferences
    private String getBoggleGridSize() {
        Log.v(DEBUG_TAG,"getBoggleGridSize()");
        boggleGridSize = boggleSharedPreferences.getString(BOGGLE_GRID_SIZE_KEY, "4");
        Log.i(DEBUG_TAG,"Using grid size of:" + boggleGridSize);
        return boggleGridSize;
    }

    // we have a new high score
    public void setBoggleGridSize(String newGridSize) {
        Log.v(DEBUG_TAG,"setBoggleGridSize()");
        SharedPreferences.Editor spEditor = boggleSharedPreferences.edit();
        spEditor.putString(BOGGLE_GRID_SIZE_KEY, newGridSize);
        spEditor.commit();
        boggleGridSize = newGridSize;
        Log.i(DEBUG_TAG,"New Grid Size is:" + newGridSize);
    }

    // anonymous listener for settings
    private OnSharedPreferenceChangeListener preferenceChangeListener =
            new OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    Log.v(DEBUG_TAG, "in preferenceChangeListener()");

                    // user changed app settings
                    preferencesChanged = true;

                    // # of choices to display has changed
                    String choices = null;
                    Log.i(DEBUG_TAG, "key:" + key.toString());
                    if (key.equals(BOGGLE_GRID_SIZE_KEY)) {
                        choices = sharedPreferences.getString(BOGGLE_GRID_SIZE_KEY, DEFAULT_GRID_SIZE);
                        Log.i(DEBUG_TAG, "Boggle Grid changed to " + choices);
                        setBoggleGridSize(choices);
                    }

                    // clean out the toast message before reusing
                    sbToastMessage.delete(0,sbToastMessage.length());
                    sbToastMessage = sbToastMessage.append(getString(R.string.reconfiguring_boggle));
                    sbToastMessage = sbToastMessage.append(":");
                    sbToastMessage = sbToastMessage.append(choices);
                    sbToastMessage = sbToastMessage.append("x");
                    sbToastMessage = sbToastMessage.append(choices);
                    sbToastMessage = sbToastMessage.append(" grid");
                    Toast.makeText(WelcomeActivity.this, sbToastMessage.toString(), Toast.LENGTH_LONG).show();

                }   // end of onSharedPreferenceChanged
            };  // end of preferenceChangeListener, don't forget the ";"

}
