package com.mastergenova.controldiabeteswatch;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private UUID MY_UUID;
    private String TAG = "";

    public ConnectThread(BluetoothDevice device){
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        }catch (IOException e){
            Log.e(TAG, "Socket's create() method failed", e);
        }
    }

    public void run(){

    }

    public void cancel(){

    }
}
