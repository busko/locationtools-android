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
import android.app.Activity;
import android.location.Location;

public interface IGpsLoggerServiceClient
{

	
	/**
	 * New message from the service to be displayed on the activity form.
	 * @param message
	 */
	public void OnStatusMessage(String message);
	
	/**
	 * A new location fix has been obtained.
	 * @param loc
	 */
	public void OnLocationUpdate(Location loc);
	
	/**
	 * New satellite count has been obtained.
	 * @param count
	 */
	public void OnSatelliteCount(int count);
	
	/**
	 * Asking the calling activity form to clear itself.
	 */
	public void ClearForm();
	

	
	/**
	 * Returns the activity
	 * @return
	 */
	public Activity GetActivity();

	public void displayLocationDesc(String desc);

	

}
