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

import android.app.Application;
import android.location.Location;

public class Session extends Application
{

	// ---------------------------------------------------
	// Session values - updated as the app runs
	// ---------------------------------------------------
	private static boolean gpsEnabled = true;
	private static boolean isStarted;
	private static boolean isUsingGps;
	private static String currentFileName;
	private static int satellites;
	private static boolean notificationVisible;
	private static long latestTimeStamp;
	private static Location currentLocationInfo;
	private static boolean isBound;
	private static boolean allowDescription = true;

    private static String routeNumber;
    private static String routeDescription;

	// ---------------------------------------------------

	/**
	 * @return whether GPS (satellite) is enabled
	 */
	public static boolean isGpsEnabled()
	{
		return gpsEnabled;
	}

	/**
	 * @param gpsEnabled
	 *            set whether GPS (satellite) is enabled
	 */
	public static void setGpsEnabled(boolean gpsEnabled)
	{
		Session.gpsEnabled = gpsEnabled;
	}

	/**
	 * @return whether logging has started
	 */
	public static boolean isStarted()
	{
		return isStarted;
	}

	/**
	 * @param isStarted
	 *            set whether logging has started
	 */
	public static void setStarted(boolean isStarted)
	{
		Session.isStarted = isStarted;

	}

	/**
	 * @return the isUsingGps
	 */
	public static boolean isUsingGps()
	{
		return isUsingGps;
	}

	/**
	 * @param isUsingGps
	 *            the isUsingGps to set
	 */
	public static void setUsingGps(boolean isUsingGps)
	{
		Session.isUsingGps = isUsingGps;
	}

	/**
	 * @return the currentFileName (without extension)
	 */
	public static String getCurrentFileName()
	{
		return currentFileName;
	}

	/**
	 * @param currentFileName
	 *            the currentFileName to set
	 */
	public static void setCurrentFileName(String currentFileName)
	{
		Utilities.LogInfo("Setting file name - " + currentFileName);
		Session.currentFileName = currentFileName;
	}

	/**
	 * @return the number of satellites visible
	 */
	public static int getSatelliteCount()
	{
		return satellites;
	}

	/**
	 * @param satellites
	 *            sets the number of visible satellites
	 */
	public static void setSatelliteCount(int satellites)
	{
		Session.satellites = satellites;
	}

	/**
	 * @return the notificationVisible
	 */
	public static boolean isNotificationVisible()
	{
		return notificationVisible;
	}

	/**
	 * @param notificationVisible
	 *            the notificationVisible to set
	 */
	public static void setNotificationVisible(boolean notificationVisible)
	{
		Session.notificationVisible = notificationVisible;
	}

	/**
	 * @return the currentLatitude
	 */
	public static double getCurrentLatitude()
	{
		if (getCurrentLocationInfo() != null)
		{
			return getCurrentLocationInfo().getLatitude();
		}
		else
		{
			return 0;
		}
	}

	/**
	 * Determines whether a valid location is available
	 */
	public static boolean hasValidLocation()
	{
		return (getCurrentLocationInfo() != null && getCurrentLatitude() != 0 && getCurrentLongitude() != 0);
	}

	/**
	 * @return the currentLongitude
	 */
	public static double getCurrentLongitude()
	{
		if (getCurrentLocationInfo() != null)
		{
			return getCurrentLocationInfo().getLongitude();
		}
		else
		{
			return 0;
		}
	}

	/**
	 * @return the latestTimeStamp (for location info)
	 */
	public static long getLatestTimeStamp()
	{
		return latestTimeStamp;
	}

	/**
	 * @param latestTimeStamp
	 *            the latestTimeStamp (for location info) to set
	 */
	public static void setLatestTimeStamp(long latestTimeStamp)
	{
		Session.latestTimeStamp = latestTimeStamp;
	}
	/**
	 * @param currentLocationInfo
	 *            the latest Location class
	 */
	public static void setCurrentLocationInfo(Location currentLocationInfo)
	{
		Session.currentLocationInfo = currentLocationInfo;
	}

	/**
	 * @return the Location class containing latest lat-long information
	 */
	public static Location getCurrentLocationInfo()
	{
		return currentLocationInfo;
	}

	/**
	 * @param isBound
	 *            set whether the activity is bound to the GpsLoggingService
	 */
	public static void setBoundToService(boolean isBound)
	{
		Session.isBound = isBound;
	}

	/**
	 * @return whether the activity is bound to the GpsLoggingService
	 */
	public static boolean isBoundToService()
	{
		return isBound;
	}

	public static boolean shoulAllowDescription()
	{
		return allowDescription;
	}

	public static void setAllowDescription(boolean allowDescription)
	{
		Session.allowDescription = allowDescription;
	}

    public static String getRouteNumber() {
        return routeNumber;
    }

    public static void setRouteNumber(String routeNumber) {
        Session.routeNumber = routeNumber;
    }

    public static String getRouteDescription() {
        return routeDescription;
    }

    public static void setRouteDescription(String routeDescription) {
        Session.routeDescription = routeDescription;
    }

    public static String getBusID()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
