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

public class AppSettings extends Application
{
    // ---------------------------------------------------
    // User Preferences
    // ---------------------------------------------------
    private static boolean showInNotificationBar;
    private static int     minimumDistance;
    private static int     minimumSeconds   = 1;
    private static String  newFileCreation;

    /**
     * @return the showInNotificationBar
     */
    public static boolean shouldShowInNotificationBar()
    {
        return showInNotificationBar;
    }

    /**
     * @param showInNotificationBar
     *            the showInNotificationBar to set
     */
    static void setShowInNotificationBar(boolean showInNotificationBar)
    {
        AppSettings.showInNotificationBar = showInNotificationBar;
    }

    /**
     * @return the minimumDistance
     */
    public static int getMinimumDistance()
    {
        return minimumDistance;
    }

    /**
     * @param minimumDistance
     *            the minimumDistance to set
     */
    static void setMinimumDistance(int minimumDistance)
    {
        AppSettings.minimumDistance = minimumDistance;
    }

    /**
     * @return the minimumSeconds
     */
    public static int getMinimumSeconds()
    {
        return minimumSeconds;
    }

    /**
     * @param minimumSeconds
     *            the minimumSeconds to set
     */
    static public void setMinimumSeconds(int minimumSeconds)
    {
        AppSettings.minimumSeconds = minimumSeconds;
    }
}
