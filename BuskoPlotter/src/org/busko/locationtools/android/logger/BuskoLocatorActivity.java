package org.busko.locationtools.android.logger;
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
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.busko.locationtools.android.common.Session;
import org.busko.locationtools.android.common.BaseActivity;
import org.busko.locationtools.android.locator.IGpsLoggerServiceClient;
import org.busko.locationtools.android.locator.R;
import org.busko.locationtools.android.loggers.CSVLogger;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BuskoLocatorActivity extends BaseActivity implements IGpsLoggerServiceClient
{
    public final Handler      handler              = new Handler();
    private Button            markButton;
    private TextView          markCounterDisplay;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logger);

        TextView routeText = (TextView) findViewById(R.id.text_logger_route);
        routeText.setText("Recording Route [" + Session.getRouteNumber() + "]");

        markCounterDisplay = (TextView) findViewById(R.id.location);
        markButton = (Button) findViewById(R.id.MarkButton);
        markButton.setEnabled(false);
        markButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                getLoggingService().markIt();
            }
        });

        (findViewById(R.id.ExitButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                stopLogging();
            }
        });
    }

    private void stopLogging() {
        if (getLoggingService() != null) {
            getLoggingService().stopLogging();
            getLoggingService().stopSelf();
        }

        // Try uploading data here if the radio or wireless is available
//        if (isOnline()) {
//            (new UploadRoute()).execute();
//        }

        Intent launchMainMenu = new Intent(this, MainMenuActivity.class);
        startActivity(launchMainMenu);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

//    /**
//     * Called when the menu is created.
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu)
//    {
//        MenuItem toggleItem = menu.findItem(R.id.mnuToggle);
//        if (getLoggingService() != null && getLoggingService().isLogging())
//        {
//            toggleItem.setTitle(R.string.stopTracking);
//        }
//        else
//        {
//            toggleItem.setTitle(R.string.startTracking);
//        }
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    /**
//     * Called when one of the menu items is selected.
//     */
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        boolean result = super.onOptionsItemSelected(item);
//        int itemId = item.getItemId();
//        if (itemId == R.id.mnuToggle)
//        {
//            getLoggingService().toggleLogging();
//            markButton.setEnabled(false);
//        }
//        return result || false;
//    }

    protected void DisplayLocationInfo(Location currentLocationInfo)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void OnStatusMessage(String message)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void OnLocationUpdate(Location loc)
    {
        markButton.setText(R.string.Mark);
        markButton.setEnabled(true);

        // TODO This overrides the Geocoding result - at present this is still better
        markCounterDisplay.setText(loc.getLatitude() + " - " + loc.getLongitude());
    }

    @Override
    public void OnSatelliteCount(int count)
    {
    }

    @Override
    public void ClearForm()
    {
        displayLocationDesc("");
    }

    @Override
    public Activity GetActivity()
    {
        return this;
    }

    /**
     * TODO It does not appear the Geocoding is very accurate so this doesn't really work past an initial mark
     */
    @Override
    public void displayLocationDesc(String desc)
    {
        markCounterDisplay.setText(desc.replace("\n", " - "));
    }

    @Override
    protected void addLoggers()
    {
        getLoggingService().addLogger(CSVLogger.INSTANCE);
    }
}
