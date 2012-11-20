package org.busko.locationtools.android.logger.util;
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
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import org.busko.locationtools.android.loggers.CSVLogger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Upload route or routes to the central service if connection is present.
 */
public class UploadRoute extends AsyncTask<String, Integer, String> {

    public static final String TAG = "BuskoPlotter.UploadRoute";

    private static final String ROUTEMANAGERSITE = "http://192.168.1.100:8080";

    private Context applicationContext;

    public UploadRoute(Context applicationContext) {
        this.applicationContext = applicationContext;
    }
    @Override
    protected String doInBackground(String... strings) {
        File logDirectory = CSVLogger.getLogDirectory();

        for (String filename : logDirectory.list()) {
            File file = new File(logDirectory, filename);
            if (uploadRoute(file)) {
                file.delete();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
    }

    private boolean uploadRoute(File file) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String urlString = prefs.getString("url", ROUTEMANAGERSITE) + "/api/routesubmissions";
        String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.
        String charset = "UTF-8";

        PrintWriter writer = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream output = connection.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(output, charset), true); // true = autoFlush, important!

            // Send normal param.
            String value = URLEncoder.encode(prefs.getString("username", "alphauser"), charset);
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"").append("username").append("\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=").append(charset).append(CRLF);
            writer.append(CRLF);
            writer.append(value).append(CRLF).flush();

            // Send text file.
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"fileData\"; filename=\"").append(file.getName()).append("\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=").append(charset).append(CRLF);
            writer.append(CRLF).flush();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
                for (String line; (line = reader.readLine()) != null;) {
                    writer.append(line).append(CRLF);
                }
            } finally {
                if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) {}
            }
            writer.flush();
/*
            // Send binary file.
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"fileContent\"; filename=\"").append(file.getName()).append("\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=").append(charset).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
                for (String line; (line = reader.readLine()) != null;) {
                    writer.append(line).append(CRLF);
                }
                output.flush(); // Important! Output cannot be closed. Close of writer will close output as well.
            } catch (Exception e) {
                Log.e(TAG, "Exception uploading image=" + file.getName() + ": " + e);
            } finally {
                if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) {}
            }
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of binary boundary.
*/
            // End of multipart/form-data.
            writer.append("--").append(boundary).append("--").append(CRLF);
            writer.close();
            writer = null;

            // Calling the Spring service the pages return code 302 - HTTP_MOVED_TEMP
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK || connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                Log.d(TAG, "Uploaded filename=" + file.getName());
                return true;
            }
            else {
                Log.e(TAG, "Error uploading filename=" + file.getName() + " ResponseCode=" + connection.getResponseCode());
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Exception uploading filename=" + file.getName() + ": " + e);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Exception uploading filename=" + file.getName() + ": " + e);
        } catch (IOException e) {
            Log.e(TAG, "Exception uploading filename=" + file.getName() + ": " + e);
        } finally {
            if (writer != null) writer.close();
        }
        return false;
    }
}
