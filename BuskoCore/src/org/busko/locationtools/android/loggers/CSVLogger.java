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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.busko.locationtools.android.common.Session;

import android.location.Location;
import android.os.Environment;

public enum CSVLogger implements Logger
{
    INSTANCE;
    private File csvFile;
    private String note;

    CSVLogger() 
    {
        csvFile = null;
    }
    
    @Override
    public void write(Location loc) throws Exception
    {
      write(loc, null);
    }

    @Override
    public void write(Location loc, String description) throws Exception {
      if (csvFile == null) {
          csvFile = new File(getLogDirectory(),Session.getCurrentFileName()+".csv");
      }
      if(!csvFile.exists())
      {
          writeHeader();
      }
      FileWriter fw = new FileWriter(csvFile,true);
      String data = constructData(loc, description);
      fw.write(data);
      fw.write('\n');
      fw.flush();
      fw.close();
    }

    @Override
    public void close() throws Exception {
    }


    private String constructData(Location loc, String description)
    {
        String line="";
        line+=loc.getLatitude()+","+loc.getLongitude()+","+loc.getAccuracy()+","+loc.getTime()+","+loc.getSpeed()+","+loc.getBearing();
        if (description != null)
        {
            line+=","+description;
        }
        else if(note != null)
        {
            line+=","+note;
            note = null;
        }
        return line;
    }

    private void writeHeader() throws IOException
    {
        FileWriter fw = new FileWriter(csvFile);
        fw.write("Lat,Long,Accuracy,Time,Speed,Bearing,Note\n");
        fw.write(",,,,,,[\"");
        fw.write(Session.getRouteNumber());
        fw.write("\"]-[\"");
        if (Session.getRouteDescription() != null) {
            fw.write(Session.getRouteDescription());
        }
        fw.write("\"]\n");
        fw.close();
    }
    
    @Override
    public void annotate(String description) throws Exception
    {
        note = description;
    }



    public static File getLogDirectory()
    {
        File gpxFolder = new File(Environment.getExternalStorageDirectory(), "BuskoLocator");
        if (!gpxFolder.exists())
        {
            gpxFolder.mkdirs();
        }
        return gpxFolder;
    }

}
