package com.example.airqualityindex;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

public class BluetoothUtils {

    public static BluetoothAdapter getBluetoothAdapter(Context context){
        BluetoothManager mBluetoothManager = (BluetoothManager) context.getSystemService(context.BLUETOOTH_SERVICE);
        return mBluetoothManager.getAdapter();
    }
}


// Bluetooth Estimote 1 : F1:09:A5:2A:A0:92
// Estimote 2 : FC:F9:AC:E7:3D:8A


