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

import android.location.Location;
import android.os.Environment;
import org.busko.locationtools.android.common.Session;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public enum GPXLogger implements Logger
{
    INSTANCE;
    private File gpxFile;
    private String note;

    GPXLogger()
    {
        gpxFile = null;
    }
    
    @Override
    public void write(Location loc) throws Exception
    {
      write(loc, null);
    }

    @Override
    public void write(Location loc, String description) throws Exception {
      if (gpxFile == null) {
          gpxFile = new File(getLogDirectory(),Session.getCurrentFileName()+".xml");
      }
      if(!gpxFile.exists())
      {
          writeHeader();
      }
      FileWriter fw = new FileWriter(gpxFile,true);
      String data = constructData(loc, description);
      fw.write(data);
      fw.write('\n');
      fw.flush();
      fw.close();
    }

    @Override
    public void close() throws Exception {
        writeFooter();
    }


    private String constructData(Location loc, String description)
    {
        String line = loc.getAccuracy()+","+loc.getTime()+","+loc.getSpeed()+","+loc.getBearing();
        if(note != null)
        {
            line+=","+note;
            note = null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("<trkpt lat=\"").append(loc.getLatitude()).append("\" lon=\"").append(loc.getLongitude()).append("\">");
        if (description != null) {
            builder.append("<desc>").append(description).append("</desc>");
        }
        builder.append("<cmt>").append(line).append("</cmt>");
        builder.append("</trkpt>");

        return builder.toString();
    }

    private void writeHeader() throws IOException
    {
        FileWriter fw = new FileWriter(gpxFile);
        fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        fw.write("<gpx xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:gpxdata=\"http://www.cluetrust.com/XML/GPXDATA/1/0\" xmlns=\"http://www.topografix.com/GPX/1/0\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.cluetrust.com/XML/GPXDATA/1/0 http://www.cluetrust.com/Schemas/gpxdata10.xsd\" version=\"1.0\" creator=\"" + Session.getRouteNumber() + "\">\n");
        fw.write("<author>" + Session.getRouteNumber() + "</author>\n");
        fw.write("<time>" + (new Date()) + "</time>\n");
        fw.write("<trk>\n");
        fw.write("<name>" + Session.getRouteNumber() + "</name>\n");
        fw.write("<desc>" + Session.getRouteDescription() + "</desc>\n");
        fw.write("<trkseg>");
        fw.close();
    }

    private void writeFooter() throws IOException
    {
        FileWriter fw = new FileWriter(gpxFile, true);
        fw.write("</trkseg>");
        fw.write("</trk>");
        fw.write("</gpx>");
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
