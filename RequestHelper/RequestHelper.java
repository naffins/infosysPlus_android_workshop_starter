package com.example.studentregistry;


import android.os.Handler;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

// TODO: Complete this class (to be guided)
public class RequestHelper extends Thread {

    public static interface PostRequestTask {

        // TO REMOVE IN STARTER CODE
        public void postRequestExecute(String jsonOutput, int responseCode);
    }

    private static String ipAddress = null;

    public static void setIpAddress(String ipAddress) {
        RequestHelper.ipAddress = ipAddress;
    }

    // =====START OF CODE TO BE REMOVED IN STARTER VERSION=====

    private final String path;
    private final String jsonInput;
    private final String method;
    private volatile String jsonOutput;
    private volatile int responseCode = -1;
    private PostRequestTask postRequestTask;
    private Handler handler;

    public RequestHelper(String path, String jsonInput, String method, PostRequestTask postRequestTask) {
        this.path = path;
        this.jsonInput = jsonInput;
        this.method = method;
        this.postRequestTask = postRequestTask;
        this.handler = new Handler();
    }

    // Thread's main program flow
    public void run() {

        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter writer = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;

        try {

            // Setup connection to URL
            HttpURLConnection connection = (HttpURLConnection) new URL("http://"+ipAddress+":8000"+path).openConnection();

            // Indicate method
            connection.setRequestMethod(method);

            // Indicate that we accept JSON as reply
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoInput(true);

            // If not a GET request
            if (!method.equals("GET")) {
                // Indicate content type as JSON
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                // Allow sending of content/receipt of content
                connection.setDoOutput(true);

                // Get output stream and writers
                outputStream = new BufferedOutputStream(connection.getOutputStream());
                outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");

                writer = new BufferedWriter(outputStreamWriter);

                // Write the request body content and flush to connection
                writer.write(jsonInput);
                writer.flush();
                writer.close();
                writer = null;
                outputStreamWriter.close();
                outputStreamWriter = null;
                outputStream.close();
                outputStream = null;
            }


            // Perform connection
            connection.connect();

            // Get response
            responseCode = connection.getResponseCode();

            // Get input stream if no error, or error stream if response code is an error code
            inputStream = responseCode<400? connection.getInputStream() : connection.getErrorStream();
            StringBuilder output = new StringBuilder("");

            // Get reader for output (reply from server)
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            // Keep reading (ignoring line-terminating characters)
            String read = reader.readLine();
            while (read != null) {
                output.append(read);
                read = reader.readLine();
            }

            // Close reader
            reader.close();
            reader = null;
            inputStreamReader.close();
            inputStreamReader = null;
            inputStream.close();
            inputStream = null;
            
            // Assing string to be processed after connection is done
            jsonOutput = output.toString();

            // Close connection
            connection.disconnect();
        }
        catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("PostHelper","Error: could not execute request");
                    Log.e("PostHelper","Response code: " + Integer.toString(responseCode));
                    e.printStackTrace();
                }
            });
            jsonOutput = null;

            try {
                if (outputStream!=null) outputStream.close();
                if (outputStreamWriter!=null) outputStreamWriter.close();
                if (writer!=null) writer.close();
                if (inputStream!=null) inputStream.close();
                if (inputStreamReader!=null) inputStreamReader.close();
                if (reader!=null) reader.close();
            }
            catch (Exception f) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        f.printStackTrace();
                        //Log.e("PostHelper", Arrays.toString(f.getStackTrace()));
                    }
                });
            }
        }

        // Do some post-request stuff
        handler.post(new Runnable() {
            public void run() {
                postRequestTask.postRequestExecute(jsonOutput,responseCode);
            }
        });

        // =====END OF CODE TO BE REMOVED IN STARTER VERSION=====
    }
}

