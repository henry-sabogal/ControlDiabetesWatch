package com.mastergenova.controldiabeteswatch;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SendDataActivity extends WearableActivity implements SensorEventListener {

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothService mBluetoothService = null;

    private TextView mTextView;

    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);

        Button btnSendData = (Button)findViewById(R.id.btnSendData);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null){
            Toast.makeText(this,"Device doesn't support bluetooth", Toast.LENGTH_LONG).show();
        }else{
            Intent intent = getIntent();
            mConnectedDeviceName = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);

            setupConnection();
            connectDevice(intent, true);
        }

        btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBluetoothService.getState() == BluetoothService.STATE_CONNECTED){
                    String message = "Hola mundo";
                    sendMessage(message);
                }else{
                    System.out.println("Bluetooth Not Connected");
                }
            }
        });

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s: deviceSensors){
            System.out.println("Sensor type: " + s.getName());
            System.out.println("Sensor type: " + s.getStringType());
        }

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onStart(){
        super.onStart();

        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else if(mBluetoothService == null){
            setupConnection();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mBluetoothService != null){
            mBluetoothService.stop();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mBluetoothService != null){
            if(mBluetoothService.getState() == BluetoothService.STATE_NONE){
                mBluetoothService.start();
            }
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy){
        //Do something here if sensor accuracy changes
    }

    @Override
    public final void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_HEART_RATE){
            System.out.println("Heart Rate " + (int)event.values[0]);
        }else if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            System.out.println("Step Counter " + (int)event.values[0]);
        }else{
            System.out.println("Unknown Sensor Type");
        }
    }

    private void setupConnection(){
        mBluetoothService = new BluetoothService(this, mHandler);
    }

    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case Constants.MESSAGE_STATE_CHANGE:
                    System.out.println("Message state change");
                    switch (msg.arg1){
                        case BluetoothService.STATE_CONNECTED:
                            System.out.println("Bluetooth state connected");
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            System.out.println("Bluetooth state connecting");
                            break;
                        case BluetoothService.STATE_LISTEN:
                            System.out.println("Bluetooth state listen");
                            break;
                        case BluetoothService.STATE_NONE:
                            System.out.println("Bluetooth state none");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    System.out.println("Write a message");
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    System.out.println("Connected to " + mConnectedDeviceName);
                    break;
                case Constants.MESSAGE_TOAST:
                    System.out.println("Message Toast " + msg.getData().getString(Constants.DEVICE_NAME));
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult " + requestCode);
        switch (requestCode){
            case REQUEST_CONNECT_DEVICE_SECURE:
                if(resultCode == this.RESULT_OK){
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                if(resultCode == this.RESULT_OK){
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                if(resultCode == this.RESULT_OK){
                    setupConnection();
                }else {
                    System.out.println("BT not enabled");
                    Toast.makeText(this, "BT not enabled", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure){
        String address = data.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);
        System.out.println("Address " + address);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mBluetoothService.connect(device, secure);
    }

    private void sendMessage(String message){
        if(mBluetoothService.getState() != BluetoothService.STATE_CONNECTED){
            Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        if(message.length() > 0){
            byte[] send = message.getBytes();
            System.out.println("Send Message Hello World");
            mBluetoothService.write(send);
        }
    }
}
