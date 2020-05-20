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
import android.widget.ImageView;
import android.widget.TextView;

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

    private TextView mHeartRateTextView;
    private TextView mStepCounterTextView;
    private TextView mStatusTextView;
    private TextView mAcceloremeterTextView;

    private SensorManager sensorManager;
    private Sensor mHeartRateSensor;
    private Sensor mStepCountSensor;
    private Sensor mAccelerometerSensor;

    private int heartRate = 0;
    private int stepCounter = 0;
    private int accelerometer = 0;

    private String message;

    private ImageView mBluetoothIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);

        mHeartRateTextView = (TextView)findViewById(R.id.txtHeartRate);
        mStepCounterTextView = (TextView)findViewById(R.id.txtStepCounter);
        mStatusTextView = (TextView)findViewById(R.id.txtStatus);
        mAcceloremeterTextView = (TextView)findViewById(R.id.txtAcceloremeter);

        mBluetoothIcon = (ImageView)findViewById(R.id.menuIcon4);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null){
            mStatusTextView.setText("Device doesn't support bluetooth");
            mBluetoothIcon.setImageResource(R.drawable.baseline_bluetooth_disabled_black_24);
        }else{
            Intent intent = getIntent();
            mConnectedDeviceName = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);

            setupConnection();
            connectDevice(intent, true);
        }

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s: deviceSensors){
            System.out.println("Sensor type: " + s.getName());
            System.out.println("Sensor type: " + s.getStringType());
        }
        mHeartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mStepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mAccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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
            mHeartRateTextView.setText("Heart Rate " + (int)event.values[0]);
            heartRate = (int)event.values[0];
            message = heartRate+":"+stepCounter+":"+accelerometer;
            sendMessage(message);
        }else if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            System.out.println("Step Counter " + (int)event.values[0]);
            mStepCounterTextView.setText("Step Counter " + (int)event.values[0]);
            stepCounter = (int)event.values[0];
            message = heartRate+":"+stepCounter+":"+accelerometer;
            sendMessage(message);
        }else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            System.out.println("Accelerometer: "  + (int)event.values[0]);
            mAcceloremeterTextView.setText("Accelerometer: "  + (int)event.values[0]);
            accelerometer = (int)event.values[0];
            message = heartRate+":"+stepCounter+":"+accelerometer;
            sendMessage(message);
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
                            sensorManager.registerListener(SendDataActivity.this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
                            sensorManager.registerListener(SendDataActivity.this, mStepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
                            sensorManager.registerListener(SendDataActivity.this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
                            mStatusTextView.setText("Status: Connected");
                            mBluetoothIcon.setImageResource(R.drawable.baseline_bluetooth_connected_black_24);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            System.out.println("Bluetooth state connecting");
                            mStatusTextView.setText("Status: Connecting");
                            mBluetoothIcon.setImageResource(R.drawable.baseline_bluetooth_searching_black_24);
                            break;
                        case BluetoothService.STATE_LISTEN:
                            System.out.println("Bluetooth state listen");
                            mStatusTextView.setText("Status: Listen");
                            mBluetoothIcon.setImageResource(R.drawable.baseline_bluetooth_searching_black_24);
                            break;
                        case BluetoothService.STATE_NONE:
                            System.out.println("Bluetooth state none");
                            mStatusTextView.setText("Status: None");
                            mBluetoothIcon.setImageResource(R.drawable.baseline_bluetooth_black_24);
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
                    mStatusTextView.setText("Connected to " + mConnectedDeviceName);
                    mBluetoothIcon.setImageResource(R.drawable.baseline_bluetooth_connected_black_24);
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
                    mStatusTextView.setText("BT not enabled");
                    mBluetoothIcon.setImageResource(R.drawable.baseline_bluetooth_disabled_black_24);
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
            mStatusTextView.setText("Status: Not Connected");
            mBluetoothIcon.setImageResource(R.drawable.baseline_bluetooth_disabled_black_24);
            return;
        }

        if(message.length() > 0){
            byte[] send = message.getBytes();
            mBluetoothService.write(send);
        }
    }
}
