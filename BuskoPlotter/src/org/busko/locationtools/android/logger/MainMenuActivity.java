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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import org.busko.locationtools.android.locator.R;
import org.busko.locationtools.android.logger.util.UploadRoute;
import org.busko.locationtools.android.loggers.CSVLogger;

import java.io.*;

/**
 * Simple menu system to show available tracking options.
 */
public class MainMenuActivity extends BaseMenuActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);

        (findViewById(R.id.button_mainmenu_recordroute)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                recordRoute();
            }
        });

        (findViewById(R.id.button_mainmenu_markstop)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                markStop();
            }
        });

        Button uploadButton = (Button) findViewById(R.id.button_mainmenu_upload);
        File logDirectory = CSVLogger.getLogDirectory();
        String[] list = logDirectory.list();
        if (list != null && list.length > 0) {
            String strFormat = getResources().getString(R.string.mainmenu_button_uploadFormat);
            uploadButton.setText(String.format(strFormat, logDirectory.list().length));
            if (isOnline()) {
                uploadButton.setEnabled(true);
            }
            else {
                uploadButton.setText(uploadButton.getText() + "\n" + getResources().getString(R.string.mainmenu_button_uploadOffline));
            }
        }
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                upload();
            }
        });

        (findViewById(R.id.button_mainmenu_exit)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                exit();
            }
        });
    }

    /**
     * Want to check GPS is enabled or pointless entering data.
     */
    private void recordRoute() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent launchRecordRoute = new Intent(this, RecordRouteActivity.class);
            startActivity(launchRecordRoute);
        }
        else {
            showGPSDisabledAlertToUser();
        }
    }

    private void markStop() {
        Toast.makeText(this, "We are working on this...", Toast.LENGTH_LONG).show();
    }

    private void upload() {
        if (!isOnline()) return;

        // Try uploading data here if the radio or wireless is available
        (new UploadRoute(getApplicationContext())).execute("cdmein");

        // TODO Not ideal just setting this to uploaded but that will do for now
        Button uploadButton = (Button) findViewById(R.id.button_mainmenu_upload);
        uploadButton.setText(R.string.mainmenu_button_upload);
        uploadButton.setEnabled(false);
    }

    /**
     * TODO Needs to ensure the GPS service is shut down to avoid battery drain.
     */
    private void exit() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
            .setCancelable(false)
            .setPositiveButton("Goto Settings Page To Enable GPS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}