package org.busko.locationtools.android.common;
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
import org.busko.locationtools.android.locator.GpsLoggingService;
import org.busko.locationtools.android.locator.IGpsLoggerServiceClient;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

public abstract class BaseActivity extends Activity implements IGpsLoggerServiceClient
{
    private static Intent     serviceIntent;
    private GpsLoggingService loggingService;
    private ServiceConnection gpsServiceConnection = new ServiceConnection()
    {
        public void onServiceDisconnected(ComponentName name)
        {
            loggingService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service)
        {
            loggingService = ((GpsLoggingService.GpsLoggingBinder) service)
                    .getService();
            GpsLoggingService.setServiceClient(BaseActivity.this);
            
            addLoggers();
            loggingService.startLogging();
            if (Session.isStarted())
            {
                // DisplayLocationInfo(Session.getCurrentLocationInfo());
            }
        }
    };

    public static Intent getServiceIntent()
    {
        return serviceIntent;
    }

    public static void setServiceIntent(Intent serviceIntent)
    {
        BaseActivity.serviceIntent = serviceIntent;
    }
    protected abstract void addLoggers();
    

    @Override
    protected void onStart()
    {
        super.onStart();
        StartAndBindService();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        StartAndBindService();
    }

    /**
     * Starts the service and binds the activity to it.
     */
    private void StartAndBindService()
    {
        Utilities.LogDebug("StartAndBindService - binding now");
        setServiceIntent(new Intent(this, GpsLoggingService.class));
        // Start the service in case it isn't already running
        startService(getServiceIntent());
        // Now bind to service
        bindService(getServiceIntent(), gpsServiceConnection, Context.BIND_AUTO_CREATE);
        Session.setBoundToService(true);
    }

    /**
     * Stops the service if it isn't logging. Also unbinds.
     */
    private void StopAndUnbindServiceIfRequired()
    {
        if (Session.isBoundToService())
        {
            unbindService(gpsServiceConnection);
            Session.setBoundToService(false);
        }

        if (!Session.isStarted())
        {
            Utilities.LogDebug("StopServiceIfRequired - Stopping the service");
            // serviceIntent = new Intent(this, GpsLoggingService.class);
            stopService(getServiceIntent());
        }
    }

    @Override
    protected void onPause()
    {

        StopAndUnbindServiceIfRequired();
        super.onPause();
    };

    @Override
    protected void onDestroy()
    {
        StopAndUnbindServiceIfRequired();
        super.onDestroy();
    }

    public GpsLoggingService getLoggingService()
    {
        return loggingService;
    }

    public ServiceConnection getGpsServiceConnection()
    {
        return gpsServiceConnection;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        Utilities.LogInfo("Option item selected - " + String.valueOf(item.getTitle()));

        if (itemId == R.id.mnuExit)
        {
            getLoggingService().stopLogging();
            getLoggingService().stopSelf();
            System.exit(0);
        }
        
        return false;
    }
*/
	@Override
	public void OnStatusMessage(String message)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnLocationUpdate(Location loc)
	{
		// TODO Auto-generated method stub
		
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
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Utilities.PopulateAppSettings(this);
	}
}
