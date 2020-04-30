package com.mastergenova.controldiabeteswatch;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.BluetoothCallback;
import me.aflak.bluetooth.interfaces.DiscoveryCallback;

public class MainActivity extends WearableActivity {

    private Bluetooth bluetooth;


    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    private ArrayList<BluetoothDevice> devicesList;
    private ArrayList<String> addressList;

    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            Toast.makeText(this,"Device doesn't support bluetooth", Toast.LENGTH_LONG).show();
        }else{
            if(!bluetoothAdapter.isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }else{
                System.out.println("Bluetooht enable");
            }
        }

       /* bluetooth = new Bluetooth(this);
        bluetooth.setBluetoothCallback(bluetoothCallback);

        recyclerView = (RecyclerView) findViewById(R.id.devices_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        devicesList = new ArrayList<BluetoothDevice>();
        mAdapter = new DevicesAdapter(devicesList, new OnItemClickListener() {
            @Override
            public void onItemClick(BluetoothDevice item) {
                System.out.println("Click on " + item.getName());
                System.out.println("Click on device with address " +  item.getAddress());
            }
        });
        recyclerView.setAdapter(mAdapter);

        addressList = new ArrayList<String>();

        Button btnScan = (Button)findViewById(R.id.button_scan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetooth.isEnabled()){
                    scanDevices();
                }else{
                    bluetooth.showEnableDialog(MainActivity.this);
                }
            }
        });*/

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onStart(){
        super.onStart();
        /*bluetooth.onStart();
        if(bluetooth.isEnabled()){
            //scanDevices();
        }else{
            bluetooth.showEnableDialog(MainActivity.this);
        }*/
    }

    @Override
    protected void onStop(){
        super.onStop();
        //bluetooth.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //bluetooth.onActivityResult(requestCode, resultCode);

        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                System.out.println("Listo");
            }else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Please enable bluetooht to use this application", Toast.LENGTH_LONG).show();
            }
        }

    }

    /*private BluetoothCallback bluetoothCallback = new BluetoothCallback() {
        @Override
        public void onBluetoothTurningOn() {

        }

        @Override
        public void onBluetoothOn() {
            System.out.println("Bluetooth On");
            scanDevices();
        }

        @Override
        public void onBluetoothTurningOff() {

        }

        @Override
        public void onBluetoothOff() {

        }

        @Override
        public void onUserDeniedActivation() {

        }
    };

    private void scanDevices(){
        bluetooth.setDiscoveryCallback(new DiscoveryCallback() {
            @Override
            public void onDiscoveryStarted() {
                System.out.println("OnDiscovery Started");
            }

            @Override
            public void onDiscoveryFinished() {
                System.out.println("OnDiscovery Finished");
            }

            @Override
            public void onDeviceFound(BluetoothDevice device) {
                System.out.println("DeviceFound");
                System.out.println(device.getAddress());
                if(!addressList.contains(device.getAddress())){
                    addressList.add(device.getAddress());
                    devicesList.add(device);
                    mAdapter.notifyItemInserted(devicesList.size()-1);
                }
            }

            @Override
            public void onDevicePaired(BluetoothDevice device) {

            }

            @Override
            public void onDeviceUnpaired(BluetoothDevice device) {

            }

            @Override
            public void onError(int errorCode) {

            }
        });

        bluetooth.startScanning();


    }*/
}
