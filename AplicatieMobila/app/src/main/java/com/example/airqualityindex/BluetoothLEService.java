package com.example.airqualityindex;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BluetoothLEService extends Service {


    public final static String ACTION_GATT_CONNECTED =
            "com.example.airqualityindex.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.airqualityindex.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.airqualityindex.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.airqualityindex.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.airqualityindex.le.EXTRA_DATA";



    public final static UUID UUID_AirQuality_LEVEL =
            UUID.fromString(SampleGattAttributes.CHARACTERISTIC_SPEC_DATA_UUID);

    public final static UUID UUID_BME_DATA = UUID.fromString(SampleGattAttributes.CHARACTERISTIC_BME_DATA_UUID);
    public final static UUID UUID_PMS_DATA = UUID.fromString(SampleGattAttributes.CHARACTERISTIC_PMS_DATA_UUID);


    /*public final static UUID UUID_AirQuality_LEVEL =
            UUID.fromString(SampleGattAttributes.CHARACTERISTIC_SENSORS_DATA_UUID);*/

    public final static UUID UUID_Battery_LEVEL =
            UUID.fromString(SampleGattAttributes.UUID_BATTERY_LEVEL_UUID);

    private static final String TAG = "BluetoothLEService";

    private static final int STATE_DISCONNECT = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    IBinder mBinder = new LocalBinder();
    private int mConnectionState = STATE_DISCONNECT;
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothGatt mBluetoothGatt;
    private String bluetoothAddress;


    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            Log.d(TAG, "onConnectionStateChange " + newState);

            String intentAction;
            if(newState == BluetoothProfile.STATE_CONNECTED)
            {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");

                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            }

            else if(newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECT;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            Log.d(TAG, "onServiceDiscovered " + status);
            if(status == BluetoothGatt.GATT_SUCCESS)
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            else
                Log.w(TAG, "onServiceDIscovered received: " + status);

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            Log.d(TAG, "onCharacteristicRead " + status);
            if(status == BluetoothGatt.GATT_SUCCESS){
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic); // SE TRIMIT DATELE din caracteristica  SPRE GUI
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            Log.d(TAG, "onCharacteristicWrite " + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            Log.d(TAG, "onCharacteristicChanged");
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorRead " + status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorWrite " + status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.d(TAG, "onReliableWriteCompleted " + status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.d(TAG, "onReadRemoteRssi " + status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.d(TAG, "onMtuChanged " + status);
        }
    };

    public BluetoothLEService() {
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    private void broadcastUpdate(final String action) // trimit spre MainACtivity rezultatul
    {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) { // trimit spre MainACtivity rezultatul
        final Intent intent = new Intent(action);
        String[] valueToSend_Battery = new String[2];
        if (UUID_AirQuality_LEVEL.equals(characteristic.getUuid())) {
            int format = BluetoothGattCharacteristic.FORMAT_UINT32;
           // int sensorValue = characteristic.getValue()[2]; // trimit spre GUI doar byte-ul 2 din frame-ul trimis de dispozitiv

            //Get The Sensor index

            //int sensorIndex = characteristic.getIntValue(format, 0);
            //String stringValue = characteristic.getStringValue(2);

            byte[] receivedBytes = characteristic.getValue();
            String inputString = new String(Arrays.copyOfRange(receivedBytes, 1, 5), Charset.forName("US-ASCII"));
            String[] sensorValues = new String[5];
            if(receivedBytes.length == 17) // get SPEC sensor values - in ppb
            {
                int CO_Value = AqiUtils.GetIntFromByteArray(new byte[]{receivedBytes[1], receivedBytes[2], receivedBytes[3], receivedBytes[4]});
                int NO2_Value = AqiUtils.GetIntFromByteArray(new byte[]{receivedBytes[5], receivedBytes[6], receivedBytes[7], receivedBytes[8]});
                int SO2_Value = AqiUtils.GetIntFromByteArray(new byte[]{receivedBytes[9], receivedBytes[10], receivedBytes[11], receivedBytes[12]});
                int O3_Value = AqiUtils.GetIntFromByteArray(new byte[]{receivedBytes[13], receivedBytes[14], receivedBytes[15], receivedBytes[16]});
                //AqiUtils.GetIntFromByteArray(Arrays.copyOfRange(receivedBytes, 5, 8));
                sensorValues[0] = "SensorValues";
                sensorValues[1] =  String.valueOf(CO_Value);
                sensorValues[2] =  String.valueOf(NO2_Value);
                sensorValues[3] =  String.valueOf(SO2_Value);
                sensorValues[4] =  String.valueOf(O3_Value);

                //final int battery_level = characteristic.getIntValue(format, 0);
                intent.putExtra(EXTRA_DATA, sensorValues); // pot sa creez si alt string precum EXTRA_DATA ca sa trimit caracteristici dupa nume/UUID
                sendBroadcast(intent);
            }
        }
        if(UUID_BME_DATA.equals(characteristic.getUuid())) {
            // 1- 2 ,  3 - 2,  5 - 4
            byte[] receivedBytes = characteristic.getValue();
            String[] sensorValues = new String[4];
           // if (receivedBytes.length == 18) // get SPEC sensor values - in ppb

            int TEMP_Value = AqiUtils.GetIntFromByteArray(new byte[]{0,0,receivedBytes[1],receivedBytes[2]});
            int HUMID_Value = AqiUtils.GetIntFromByteArray(new byte[]{0,0, receivedBytes[3], receivedBytes[4]});
            int PRESSURE_Value = AqiUtils.GetIntFromByteArray(new byte[]{receivedBytes[5], receivedBytes[6], receivedBytes[7], receivedBytes[8]});
            //AqiUtils.GetIntFromByteArray(Arrays.copyOfRange(receivedBytes, 5, 8));
            sensorValues[0] = "SensorValuesBME";
            sensorValues[1] = String.valueOf(TEMP_Value);
            sensorValues[2] = String.valueOf(HUMID_Value);
            sensorValues[3] = String.valueOf(PRESSURE_Value);

            //final int battery_level = characteristic.getIntValue(format, 0);
            intent.putExtra(EXTRA_DATA, sensorValues); // pot sa creez si alt string precum EXTRA_DATA ca sa trimit caracteristici dupa nume/UUID
            sendBroadcast(intent);
        }
        if (UUID_PMS_DATA.equals(characteristic.getUuid()))
        {
            //TO DO - no data available currently
        }
        if (UUID_Battery_LEVEL.equals(characteristic.getUuid())) {
            int format = BluetoothGattCharacteristic.FORMAT_UINT8;
            //int value = characteristic.getValue()[2];
            final int battery_level = characteristic.getIntValue(format, 0);

            valueToSend_Battery[0] = "BATTERY";
            valueToSend_Battery[1] = String.valueOf(battery_level);

            intent.putExtra(EXTRA_DATA, valueToSend_Battery);
            sendBroadcast(intent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        close();
        return super.onUnbind(intent);
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d(TAG, "Bluetooth adapter not initialize");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public boolean initialize() { // Check https://github.com/googlearchive/android-BluetoothLeGatt/blob/master/Application/src/main/java/com/example/android/bluetoothlegatt/BluetoothLeService.java
        mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter(this);
        return true;
    }

    public boolean connect(@NonNull String address)  // Check https://github.com/googlearchive/android-BluetoothLeGatt/blob/master/Application/src/main/java/com/example/android/bluetoothlegatt/BluetoothLeService.java
    {
        if(mBluetoothAdapter != null && address.equals(bluetoothAddress) && mBluetoothGatt != null)
        {
            if(mBluetoothGatt.connect())
            {
                mConnectionState = STATE_CONNECTING;
                return true;
            }
            else
            {
                return false;
            }
        }
        final BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
        if(bluetoothDevice == null)
        {
            Log.w(TAG, "Device not found");
            return false;
        }
        mBluetoothGatt = bluetoothDevice.connectGatt(this, false, bluetoothGattCallback);
        bluetoothAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    // For Read and SetNotification - check : https://github.com/googlearchive/android-BluetoothLeGatt/blob/master/Application/src/main/java/com/example/android/bluetoothlegatt/BluetoothLeService.java
    public void readCharacteristic(@NonNull BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        mBluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);
    }

    public void writeCharacteristic(@NonNull BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
    }

    public void setCharacteristicNotification(@NonNull BluetoothGattCharacteristic characteristic, boolean enabled) {
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }



    public class LocalBinder extends Binder {
        BluetoothLEService getService() {
            return BluetoothLEService.this;
        }
    }
}

