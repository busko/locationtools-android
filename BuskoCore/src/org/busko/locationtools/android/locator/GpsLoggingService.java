package org.busko.locationtools.android.locator;
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
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.busko.locationtools.android.common.AppSettings;
import org.busko.locationtools.android.common.Session;
import org.busko.locationtools.android.common.Utilities;
import org.busko.locationtools.android.loggers.Logger;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.*;
import android.os.*;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;

public class GpsLoggingService extends Service
{
    private static NotificationManager     gpsNotifyManager;
    private static int                     NOTIFICATION_ID;

    /**
     * General all purpose handler used for updating the UI from threads.
     */
    public final Handler                   handler  = new Handler();
    private final IBinder                  mBinder  = new GpsLoggingBinder();
    private static IGpsLoggerServiceClient mainServiceClient;

    public static final String             LOCATION = "location";
    public static final String             ADDRESS  = "address";

    // ---------------------------------------------------
    // Helpers and managers
    // ---------------------------------------------------
    private GeneralLocationListener        gpsLocationListener;
    LocationManager                        gpsLocationManager;
    private boolean                        logging;
    Set<Logger>                            loggers  = new HashSet<Logger>();
    private Location                       lastLocation;
	private WakeLock	wl;

    // ---------------------------------------------------
    /**
     * Provides a connection to the GPS Logging Service
     */
    private class GeocoderHandler extends Handler
    {

        @Override
        public void handleMessage(Message message)
        {
            Address address = null;
            Location location = null;
            switch (message.what)
            {
            case 1:
                Bundle bundle = message.getData();
                address = bundle.getParcelable(ADDRESS);
                location = bundle.getParcelable(LOCATION);
                noteAddress(location, address);

                break;
            default:
            }
        }
    }

    public static void getAddressFromLocation(final Location location, final Context context, final Handler handler)
    {
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                Address address = null;
                try
                {
                    List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (list != null && list.size() > 0)
                    {
                        address = list.get(0);
                    }
                }
                catch (IOException e)
                {
                    Utilities.LogError("Impossible to connect to Geocoder", e);
                }
                finally
                {
                    Message msg = Message.obtain();
                    msg.setTarget(handler);
                    msg.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ADDRESS, address);
                    bundle.putParcelable(LOCATION, location);
                    msg.setData(bundle);
                    msg.sendToTarget();
                }
            }
        };
        thread.start();
    }

    public void noteAddress(Location location, Address address)
    {
        String desc = "";
        String logged = "\"";
        if (address != null)
        {
            int numLines = address.getMaxAddressLineIndex();
            for (int i = 0; i < numLines; i++)
            {
                if (i > 0)
                {
//                    desc += ",\n";
                    desc += ",";
                    logged += ",";
                }
                desc += address.getAddressLine(i);
                logged += address.getAddressLine(i);
            }
            logged += "\"";
        }
        else
        {
            desc = "No Address Found";
            logged = desc;
        }

        WriteToFile(location, desc);

        mainServiceClient.displayLocationDesc(desc);
    }

    public void markIt()
    {
        getAddressFromLocation(lastLocation, this, new GeocoderHandler());
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        Utilities.LogDebug("GpsLoggingService.onBind called");
        return mBinder;
    }

    @Override
    public void onCreate()
    {
        Utilities.LogDebug("GpsLoggingService.onCreate called");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String lang = prefs.getString("locale_override", "");

        if (!lang.equalsIgnoreCase(""))
        {
            Utilities.LogVerbose("Setting app to user specified locale: " + lang);
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }

        resetCurrentFileName();

        // csvLogger = new CSVLogger();
        Utilities.LogInfo("GPSLoggerService created");
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        Utilities.LogDebug("GpsLoggingService.onStart called");
        handleIntent(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        Utilities.LogDebug("GpsLoggingService.onStartCommand called");
        handleIntent(intent);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy()
    {
        Utilities.LogWarning("GpsLoggingService is being destroyed by Android OS.");
        mainServiceClient = null;
        super.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        Utilities.LogWarning("Android is low on memory.");
        super.onLowMemory();
    }

    private void handleIntent(Intent intent)
    {

        Utilities.LogDebug("GpsLoggingService.handleIntent called");
        // SetupAutoEmailTimers();

        Utilities.LogDebug("Null intent? " + String.valueOf(intent == null));

        if (intent != null)
        {
            Bundle bundle = intent.getExtras();

            if (bundle != null)
            {
                boolean startRightNow = true;// bundle.getBoolean("immediate");
                boolean alarmWentOff = bundle.getBoolean("alarmWentOff");

                Utilities.LogDebug("startRightNow - " + String.valueOf(startRightNow));

                Utilities.LogDebug("alarmWentOff - " + String.valueOf(alarmWentOff));

                if (startRightNow)
                {
                    Utilities.LogInfo("Auto starting logging");

                    startLogging();
                }

                if (alarmWentOff)
                {

                    Utilities.LogDebug("setEmailReadyToBeSent = true");

                }

            }
        }
        else
        {
            // A null intent is passed in if the service has been killed and
            // restarted.
            Utilities.LogDebug("Service restarted with null intent. Start logging.");
            startLogging();

        }
    }

    /**
     * Can be used from calling classes as the go-between for methods and properties.
     * 
     */
    public class GpsLoggingBinder extends Binder
    {
        public GpsLoggingService getService()
        {
            Utilities.LogDebug("GpsLoggingBinder.getService called.");
            return GpsLoggingService.this;
        }
    }

    /**
     * Sets the activity form for this service. The activity form needs to implement IGpsLoggerServiceClient.
     * 
     * @param mainForm
     */
    public static void setServiceClient(IGpsLoggerServiceClient mainForm)
    {
        mainServiceClient = mainForm;
    }

    /**
     * Resets the form, resets file name if required, reobtains preferences
     */
    public void startLogging()
    {
        Utilities.LogDebug("GpsLoggingService.StartLogging called");

        if (Session.isStarted())
        {
            return;
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();
        Utilities.LogInfo("Starting logging procedures");
        startForeground(NOTIFICATION_ID, null);
        Session.setStarted(true);

        notifyStatusBar();
        startGpsManager();

        resetCurrentFileName();
        logging = true;
    }

    /**
     * Stops logging, removes notification, stops GPS manager, stops email timer
     */
    public void stopLogging()
    {
        logging = false;

        for (Logger logger : loggers) {
            try {
                logger.close();
            } catch (Exception e) {
                Utilities.LogError("stopLogging", e);
            }
        }

        Utilities.LogDebug("GpsLoggingService.StopLogging called");
        if(wl != null)
        {
        	wl.release();
        	wl = null;
        }
        Utilities.LogInfo("Stopping logging");
        Session.setStarted(false);
        // Email log file before setting location info to null
        Session.setCurrentLocationInfo(null);
        stopForeground(true);

        removeNotification();
        stopGpsManager();
    }

    /**
     * Manages the notification in the status bar
     */
    private void notifyStatusBar()
    {

        Utilities.LogDebug("GpsLoggingService.Notify called");
        if (AppSettings.shouldShowInNotificationBar())
        {
            gpsNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            showNotification();
        }
        else
        {
            removeNotification();
        }
    }

    /**
     * Hides the notification icon in the status bar if it's visible.
     */
    private void removeNotification()
    {
        Utilities.LogDebug("GpsLoggingService.RemoveNotification called");
        try
        {
            if (Session.isNotificationVisible())
            {
                gpsNotifyManager.cancelAll();
            }
        }
        catch (Exception ex)
        {
            Utilities.LogError("RemoveNotification", ex);
        }
        finally
        {
            // notificationVisible = false;
            Session.setNotificationVisible(false);
        }
    }

    /**
     * Shows a notification icon in the status bar for GPS Logger
     */
    private void showNotification()
    {
        Utilities.LogDebug("GpsLoggingService.ShowNotification called");
        // What happens when the notification item is clicked
        Intent contentIntent = new Intent(this, mainServiceClient.getClass());

        PendingIntent pending = PendingIntent.getActivity(getBaseContext(), 0, contentIntent,
                android.content.Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification nfc = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
        nfc.flags |= Notification.FLAG_ONGOING_EVENT;

        NumberFormat nf = new DecimalFormat("###.######");

        String contentText = getString(R.string.gpslogger_still_running);
        if (Session.hasValidLocation())
        // if (currentLatitude != 0 && currentLongitude != 0)
        {
            contentText = nf.format(Session.getCurrentLatitude()) + "," + nf.format(Session.getCurrentLongitude());
        }

        nfc.setLatestEventInfo(getBaseContext(), getString(R.string.gpslogger_still_running), contentText, pending);

        gpsNotifyManager.notify(NOTIFICATION_ID, nfc);
        Session.setNotificationVisible(true);
    }

    /**
     * Starts the location manager. There are two location managers - GPS and Cell Tower. This code determines which manager to request updates from based on user preference and whichever is enabled.
     * If GPS is enabled on the phone, that is used. But if the user has also specified that they prefer cell towers, then cell towers are used. If neither is enabled, then nothing is requested.
     */
    private void startGpsManager()
    {
        Utilities.LogDebug("GpsLoggingService.StartGpsManager");

        gpsLocationListener = new GeneralLocationListener(this);

        gpsLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, AppSettings.getMinimumSeconds() * 1000,
                AppSettings.getMinimumDistance(), gpsLocationListener);

        gpsLocationManager.addGpsStatusListener(gpsLocationListener);
        /*
         * towerLocationManager.requestLocationUpdates(LocationManager. NETWORK_PROVIDER, AppSettings.getMinimumSeconds() * 1000, AppSettings.getMinimumDistance(), towerLocationListener);
         */
        SetStatus(R.string.started);
    }

    /**
     * Stops the location managers
     */
    private void stopGpsManager()
    {

        Utilities.LogDebug("GpsLoggingService.StopGpsManager");

        if (gpsLocationListener != null)
        {
            gpsLocationManager.removeUpdates(gpsLocationListener);
            gpsLocationManager.removeGpsStatusListener(gpsLocationListener);
        }

        SetStatus(getString(R.string.stopped));
    }

    /**
     * Sets the current file name based on user preference.
     */
    private void resetCurrentFileName()
    {
        Utilities.LogDebug("GpsLoggingService.ResetCurrentFileName called");

        String newFileName;
        // 20100114.gpx
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        newFileName = sdf.format(new Date());
        Session.setCurrentFileName(newFileName);
    }

    /**
     * Gives a status message to the main service client to display
     */
    void SetStatus(String status)
    {
        if (IsMainFormVisible())
        {
            mainServiceClient.OnStatusMessage(status);
        }
    }

    /**
     * Gets string from given resource ID, passes to SetStatus(String)
     * 
     * @param stringId
     */
    private void SetStatus(int stringId)
    {
        String s = getString(stringId);
        SetStatus(s);
    }

    /**
     * Stops location manager, then starts it.
     */
    void RestartGpsManagers()
    {
        Utilities.LogDebug("GpsLoggingService.RestartGpsManagers");
        stopGpsManager();
        startGpsManager();
    }

    /**
     * This event is raised when the GeneralLocationListener has a new location. This method in turn updates notification, writes to file, reobtains preferences, notifies main service client and
     * resets location managers.
     * 
     * @param loc
     */
    void OnLocationChanged(Location loc)
    {

        // Don't do anything until the proper time has elapsed
        long currentTimeStamp = System.currentTimeMillis();
        if ((currentTimeStamp - Session.getLatestTimeStamp()) < (AppSettings.getMinimumSeconds() * 1000))
        {
            return;
        }
        lastLocation = loc;
        Utilities.LogInfo("New location obtained");
        Session.setLatestTimeStamp(System.currentTimeMillis());
        Session.setCurrentLocationInfo(loc);
        notifyStatusBar();
        WriteToFile(loc, null);
        // ResetManagersIfRequired();

        if (IsMainFormVisible())
        {
            mainServiceClient.OnLocationUpdate(loc);
        }
    }

    public void addLogger(Logger l)
    {
        loggers.add(l);
    }

    /**
     * Calls file helper to write a given location to a file.
     * 
     * @param loc
     */
    private void WriteToFile(Location loc, String description)
    {
        try
        {
            for (Logger l : loggers)
            {
                l.write(loc, description);
            }
            Session.setAllowDescription(true);
        }
        catch (Exception e)
        {
            SetStatus(R.string.could_not_write_to_file);
        }

    }

    /**
     * Informs the main service client of the number of visible satellites.
     * 
     * @param count
     */
    void SetSatelliteInfo(int count)
    {
        if (IsMainFormVisible())
        {
            mainServiceClient.OnSatelliteCount(count);
        }
    }

    private boolean IsMainFormVisible()
    {
        return mainServiceClient != null;
    }

    public boolean isLogging()
    {
        return logging;
    }

    public void toggleLogging()
    {
        if (logging)
        {
            stopLogging();
        }
        else
        {
            startLogging();
        }
    }
}
