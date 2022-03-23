package com.example.studentregistry;


// TODO: Complete this class (to be guided)
public class RequestHelper extends Thread {

    public static interface RequestTask {

    }

    private static String ipAddress = null;

    public static void setIpAddress(String ipAddress) {
        RequestHelper.ipAddress = ipAddress;
    }
}

