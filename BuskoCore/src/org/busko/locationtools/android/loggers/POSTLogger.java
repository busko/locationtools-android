package org.busko.locationtools.android.loggers;
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
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.busko.locationtools.android.common.Utilities;

import android.location.Location;

public enum POSTLogger implements Logger
{
    INSTANCE;
    private HttpClient httpclient;

    POSTLogger()
    {
        httpclient = new DefaultHttpClient();

    }

    @Override
    public void annotate(String description) throws Exception
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void write(Location loc) throws Exception
    {
        try
        {
            HttpGet request = new HttpGet();
            String uri = Utilities.getPreferences().getString("url","http://smithfamily.org.org/wTB");
            //http://localhost:8080/onebusaway-nyc-vehicle-tracking-webapp/update-vehicle-location.do?vehicleId=DCCGoBus_Bus123&lat=-45.86752590014236&lon=170.50734043121338&dsc=DCCGoBus29_2230 
            
            String vehichleId=Utilities.getPreferences().getString("vehicle", "NoVehicleSet");
            String routeId = Utilities.getPreferences().getString("route", "NoRouteSet")+"_"+Utilities.getPreferences().getString("time", "NoTimeSet");
            uri+="?vehicleId="+vehichleId+"&lat="+loc.getLatitude()+"&lon="+loc.getLongitude()+"&dsc="+routeId;
            
            request.setURI(new URI(uri));

            // Execute HTTP Post Request
            @SuppressWarnings("unused")
            HttpResponse response = httpclient.execute(request);

        }
        catch (ClientProtocolException e)
        {
            // TODO Auto-generated catch block
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
        }
    }

    @Override
    public void write(Location loc, String description) throws Exception {
        write(loc);
    }

    @Override
    public void close() throws Exception {
    }

}
