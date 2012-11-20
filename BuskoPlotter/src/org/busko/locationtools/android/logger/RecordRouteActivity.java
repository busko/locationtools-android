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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.busko.locationtools.android.common.Session;
import org.busko.locationtools.android.locator.R;

/**
 * Activity to capture the route information before tracking is started.
 *
 * The idea here is that eventually this will control accessing the backend to get back the
 * named routes that need to be tracked and to also manage recording new route names.
 */
public class RecordRouteActivity extends BaseMenuActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recordroute);

        (findViewById(R.id.button_recordroute_startrecording)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startRecording();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent launchHome = new Intent(this, MainMenuActivity.class);
        startActivity(launchHome);
    }

    private void startRecording() {
        EditText routeNumber = (EditText) findViewById(R.id.text_recordroute_routeNumber);
        if (routeNumber.getText() == null || routeNumber.getText().length() == 0) {
            Toast.makeText(this, "Please enter the route number", Toast.LENGTH_SHORT).show();
            return;
        }
        Session.setRouteNumber(routeNumber.getText().toString());

        EditText routeDescription = (EditText) findViewById(R.id.text_recordroute_routeDescription);
        if (routeDescription.getText() != null && routeDescription.getText().length() > 0) {
            Session.setRouteDescription(routeDescription.getText().toString());
        }
        else {
            Session.setRouteDescription(null);
        }

        Intent launchBuskoLocator = new Intent(this, BuskoLocatorActivity.class);
        startActivity(launchBuskoLocator);
    }
}