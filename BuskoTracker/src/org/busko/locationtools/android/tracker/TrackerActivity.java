package org.busko.locationtools.android.tracker;
/**
 * Copyright (c) 2012 Busko Trust
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import java.text.SimpleDateFormat;
import java.util.Date;

import org.busko.locationtools.android.common.AppSettings;
import org.busko.locationtools.android.common.BaseActivity;
import org.busko.locationtools.android.locator.GpsLoggingService;
import org.busko.locationtools.android.locator.IGpsLoggerServiceClient;
import org.busko.locationtools.android.locator.R;
import org.busko.locationtools.android.loggers.CSVLogger;
import org.busko.locationtools.android.loggers.POSTLogger;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class TrackerActivity extends BaseActivity implements IGpsLoggerServiceClient, OnSharedPreferenceChangeListener
{
    private static final String NOT_SET    = "Not Set";
    public final Handler        handler    = new Handler();
    private TextView            textRoute;
    private TextView            textTime;
    private TextView            textVehicle;
    private TextView            textUpdate;
    private static final int    SHOW_PREFS = 124230;
    private SimpleDateFormat    formatter  = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trackerview);
        textRoute = (TextView) findViewById(R.id.textRoute);
        textTime = (TextView) findViewById(R.id.textTime);
        textVehicle = (TextView) findViewById(R.id.textVehicle);
        textUpdate = (TextView) findViewById(R.id.textUpdate);
        refreshLoggingRate();
    }

    /**
     * Called when the menu is created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    protected void addLoggers()
    {
        getLoggingService().addLogger(POSTLogger.INSTANCE);
        getLoggingService().addLogger(CSVLogger.INSTANCE);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean result = super.onOptionsItemSelected(item);
        int itemId = item.getItemId();
        if (itemId == R.id.preferences)
        {
            showPreferences();
        }
        return result || false;
    }

    private void showPreferences()
    {
        Intent launchPrefs = new Intent(this, PreferencesActivity.class);
        startActivityForResult(launchPrefs, SHOW_PREFS);
    }

    @Override
    public void OnStatusMessage(String message)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void OnLocationUpdate(Location loc)
    {
        String formattedTime = formatter.format(new Date(loc.getTime()));
        textUpdate.setText(formattedTime);
    }

    @Override
    public void OnSatelliteCount(int count)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void ClearForm()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Activity GetActivity()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void displayLocationDesc(String desc)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if ("updateRate".equals(key))
        {
            refreshLoggingRate();
        }

        setDisplayFields();
    }

    private void refreshLoggingRate()
    {
        String val = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("updateRate", "2");

        GpsLoggingService loggingService = getLoggingService();
        if (loggingService != null)
        {
            loggingService.stopLogging();
        }
        int minimumSeconds;

        try
        {
            minimumSeconds = Integer.parseInt(val);
            AppSettings.setMinimumSeconds(minimumSeconds);
        }
        catch (NumberFormatException e)
        {
            // Swallow
        }
        if (loggingService != null)
        {
            loggingService.startLogging();
        }
    }

    private void setDisplayFields()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        textRoute.setText(prefs.getString("route", NOT_SET));
        textTime.setText(prefs.getString("time", NOT_SET));
        textVehicle.setText(prefs.getString("vehicle", NOT_SET));
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
        setDisplayFields();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);

    }

}
