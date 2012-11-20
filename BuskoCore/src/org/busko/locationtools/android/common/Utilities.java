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
import org.busko.locationtools.android.locator.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Utilities
{

	private static final int			LOGLEVEL	= 3;
	private static ProgressDialog		pd;
	private static SharedPreferences	prefs;

	public static SharedPreferences getPreferences()
	{
		return prefs;
	}

	public static void LogInfo(String message)
	{
		if (LOGLEVEL >= 3)
		{
			Log.i("GPSLogger", message);
		}

	}

	public static void LogError(String methodName, Exception ex)
	{
		try
		{
			LogError(methodName + ":" + ex.getMessage());
		}
		catch (Exception e)
		{
			/**/
		}
	}

	private static void LogError(String message)
	{
		Log.e("GPSLogger", message);

	}

	@SuppressWarnings("unused")
	public static void LogDebug(String message)
	{
		if (LOGLEVEL >= 4)
		{
			Log.d("GPSLogger", message);
		}
	}

	public static void LogWarning(String message)
	{
		if (LOGLEVEL >= 2)
		{
			Log.w("GPSLogger", message);
		}
	}

	@SuppressWarnings("unused")
	public static void LogVerbose(String message)
	{
		if (LOGLEVEL >= 5)
		{
			Log.v("GPSLogger", message);
		}
	}

	/**
	 * Gets user preferences, populates the AppSettings class.
	 * 
	 * @param context
	 * @return
	 */
	public static void PopulateAppSettings(Context context)
	{

		Utilities.LogInfo("Getting preferences");
		prefs = PreferenceManager.getDefaultSharedPreferences(context);

		AppSettings.setShowInNotificationBar(prefs.getBoolean("show_notification", true));

		String minimumDistanceString = prefs.getString("distance_before_logging", "0");

		if (minimumDistanceString != null && minimumDistanceString.length() > 0)
		{
			AppSettings.setMinimumDistance(Integer.valueOf(minimumDistanceString));
		}
		else
		{
			AppSettings.setMinimumDistance(Integer.valueOf(0));
		}

		String minimumSecondsString = prefs.getString("time_before_logging", "10");

		if (minimumSecondsString != null && minimumSecondsString.length() > 0)
		{
			AppSettings.setMinimumSeconds(Integer.valueOf(minimumSecondsString));
		}
		else
		{
			AppSettings.setMinimumSeconds(60);
		}

	}

	public static void ShowProgress(Context ctx, String title, String message)
	{
		if (ctx != null)
		{
			pd = new ProgressDialog(ctx, ProgressDialog.STYLE_HORIZONTAL);
			pd.setMax(100);
			pd.setIndeterminate(true);

			pd = ProgressDialog.show(ctx, title, message, true, true);
		}
	}

	public static void HideProgress()
	{
		if (pd != null)
		{
			pd.dismiss();
		}
	}

	/**
	 * Displays a message box to the user with an OK button.
	 * 
	 * @param title
	 * @param message
	 * @param className
	 *            The calling class, such as GpsMainActivity.this or
	 *            mainActivity.
	 */
	public static void MsgBox(String title, String message, Context className)
	{
		MsgBox(title, message, className, null);
	}

	/**
	 * Displays a message box to the user with an OK button.
	 * 
	 * @param title
	 * @param message
	 * @param className
	 *            The calling class, such as GpsMainActivity.this or
	 *            mainActivity.
	 * @param callback
	 *            An object which implements IHasACallBack so that the click
	 *            event can call the callback method.
	 */
	private static void MsgBox(String title, String message, Context className, final IMessageBoxCallback msgCallback)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(className).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton(className.getString(R.string.ok), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				if (msgCallback != null)
				{
					msgCallback.MessageBoxResult(which);
				}
			}
		});
		alertDialog.show();
	}

}
