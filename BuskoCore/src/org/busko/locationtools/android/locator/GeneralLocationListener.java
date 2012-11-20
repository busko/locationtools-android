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
import java.util.Iterator;

import org.busko.locationtools.android.common.Utilities;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;

class GeneralLocationListener implements LocationListener, GpsStatus.Listener
{

	private static GpsLoggingService mainActivity;

	GeneralLocationListener(GpsLoggingService activity)
	{
		Utilities.LogDebug("GeneralLocationListener constructor");
		mainActivity = activity;
	}

	/**
	 * Event raised when a new fix is received.
	 */
	public void onLocationChanged(Location loc)
	{

		
		try
		{
			if (loc != null)
			{
				Utilities.LogVerbose("GeneralLocationListener.onLocationChanged");
				mainActivity.OnLocationChanged(loc);
			}

		}
		catch (Exception ex)
		{
			Utilities.LogError("GeneralLocationListener.onLocationChanged", ex);
			mainActivity.SetStatus(ex.getMessage());
		}

	}

	public void onProviderDisabled(String provider)
	{
		Utilities.LogInfo("Provider disabled");
		Utilities.LogDebug(provider);
		mainActivity.RestartGpsManagers();
	}

	public void onProviderEnabled(String provider)
	{

		Utilities.LogInfo("Provider enabled");
		Utilities.LogDebug(provider);
		mainActivity.RestartGpsManagers();
	}

	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		if(status == LocationProvider.OUT_OF_SERVICE)
		{
			Utilities.LogDebug(provider + " is out of service");
		}
		
		if(status == LocationProvider.AVAILABLE)
		{
			Utilities.LogDebug(provider + " is available");
		}
		
		if(status == LocationProvider.TEMPORARILY_UNAVAILABLE)
		{
			Utilities.LogDebug(provider + " is temporarily unavailable");
		}
	}

	public void onGpsStatusChanged(int event)
	{

		switch (event)
		{
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Utilities.LogDebug("GPS Event First Fix");
				mainActivity.SetStatus(mainActivity.getString(R.string.fix_obtained));
				break;

			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

				Utilities.LogDebug("GPS Satellite status obtained");
				GpsStatus status = mainActivity.gpsLocationManager.getGpsStatus(null);

				int maxSatellites = status.getMaxSatellites();

				Iterator<GpsSatellite> it = status.getSatellites().iterator();
				int count = 0;
				//while (it.hasNext() && count <= maxSatellites)
				while (it.hasNext() && count <= maxSatellites)
				{
					@SuppressWarnings("unused")
					GpsSatellite s = it.next();
//					if (s.usedInFix())
//					{
						count++;
//					}
					//GpsSatellite oSat = (GpsSatellite) it.next();

					// Log.i("Main",
					// "LocationActivity - onGpsStatusChange: Satellites:"
					// + oSat.getSnr());
				}

				mainActivity.SetSatelliteInfo(count);
				break;

			case GpsStatus.GPS_EVENT_STARTED:
				Utilities.LogInfo("GPS started, waiting for fix");
				mainActivity.SetStatus(mainActivity.getString(R.string.started_waiting));
				break;

			case GpsStatus.GPS_EVENT_STOPPED:
				Utilities.LogInfo("GPS Stopped");
				mainActivity.SetStatus(mainActivity.getString(R.string.gps_stopped));
				break;

		}
	}
}
