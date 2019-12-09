package com.example.airqualityindex;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BluetoothLEService extends Service {



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
