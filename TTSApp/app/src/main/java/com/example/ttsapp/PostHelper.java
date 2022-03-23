package com.example.ttsapp;

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
public class PostHelper extends Thread {

    public static interface PostRequestTask {

    }

    private static String ipAddress = null;

    public static void setIpAddress(String ipAddress) {
        PostHelper.ipAddress = ipAddress;
    }
}

