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
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.busko.locationtools.android.locator.R;

public abstract class BaseMenuActivity extends Activity {

    private static final int SHOW_PREFS = 124230;

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

    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean result = super.onOptionsItemSelected(item);
        int itemId = item.getItemId();
        if (itemId == R.id.preferences)
        {
            showPreferences();
        }
        return result;
    }

    private void showPreferences()
    {
        Intent launchPrefs = new Intent(this, PreferencesActivity.class);
        startActivityForResult(launchPrefs, SHOW_PREFS);
    }
}