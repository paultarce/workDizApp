package com.example.airqualityindex;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.pawelkleczkowski.customgauge.CustomGauge;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import com.github.aakira.expandablelayout.ExpandableLayout;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;

import java.lang.Object;
import java.util.ArrayList;
import java.util.List;

public class AirQualityActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    @BindView(R.id.startScan)
    Button button;
    @BindView(R.id.connectDevice)
    Button connectDevice;

    @BindView(R.id.deviceState)
    TextView deviceStatus;
    @BindView(R.id.airQualityLevel)
    TextView airQualityLevel;

    @BindView(R.id.batteryLevel)
    TextView batteryLevelText;

    @BindView(R.id.deviceAddress)
    TextView deviceAddress;
    @BindView(R.id.deviceName)
    TextView deviceName;
    @BindView(R.id.serviceName)
    TextView serviceName;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    /*@BindView(R.id.toolbar)
    Toolbar toolbar;*/

    @BindView(R.id.expandableView)
    LinearLayout expandableView;

    @BindView(R.id.arrowBtn)
    Button arrowBtn;

    @BindView(R.id.cardView)
    CardView cardView;

    @BindView(R.id.gauge1)
    CustomGauge gauge1;

    @BindView(R.id.textView1)
    TextView textView1;

    @BindView(R.id.halfGauge)
    HalfGauge halfGauge;

    /*@BindView(R.id.expandableLayout1)
    ExpandableLayout expandabableLayout1;
*/
    /*@BindView(R.id.expandableButton1)
    Button expandableButton1;*/

    private boolean mScanning;

    //This class provides methods to perform scan related operations for Bluetooth LE devices. An application can scan for a particular type of Bluetooth LE devices using ScanFilter.
    // It can also request different types of callbacks for delivering the result.
    private BluetoothLeScanner bluetoothLeScanner;

    //Represents a remote Bluetooth device. A BluetoothDevice lets you create a connection with the respective device or query information about it,
    // such as the name, address, class, and bonding state.
    BluetoothDevice bluetoothDevice;


    //Represents the local device Bluetooth adapter. The BluetoothAdapter lets you perform fundamental Bluetooth tasks, such
    // as initiate device discovery, query a list of bonded (paired) devices, instantiate a BluetoothDevice using a known MAC
    // address, and create a BluetoothServerSocket to listen for connection requests from other devices, and start a scan for Bluetooth LE devices.
    //Fundamentally, this is your starting point for all Bluetooth actions. Once you have the local adapter, you can get a set of BluetoothDevice
    // objects representing all paired devices with getBondedDevices(); start device discovery with startDiscovery()
    private BluetoothAdapter mBluetoothAdapter;

    @BindView(R.id.connectService)
    Button connectService;

    //A GATT characteristic is a basic data element used to construct a GATT service, BluetoothGattService. The characteristic contains a value as
    // well as additional information and optional GATT descriptors, BluetoothGattDescriptor.
    private List<BluetoothGattCharacteristic> mNotifyCharacteristics;
    private boolean mConnected = false;

    private BluetoothLEService mBluetoothLEService;


    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLEService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState("connected");
                invalidateOptionsMenu();
                progressBar.setVisibility(View.INVISIBLE);
            } else if (BluetoothLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState("disconnected");
                unbindService(mServiceConnection);
                //clearUI(f);
            }
            else if (BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
            {
                displayGattServices(mBluetoothLEService.getSupportedGattServices());
            }
            else if (BluetoothLEService.ACTION_DATA_AVAILABLE.equals(action)) // primesc date de la sevciciul BluetoothLEService
            {
                displayData(intent.getStringExtra(BluetoothLEService.EXTRA_DATA));
            }
        }
    };

    public void setHalfGauge()
    {
        Range range = new Range();
        range.setColor(Color.parseColor("#00b20b"));
        range.setFrom(0);
        range.setTo(50.0);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#E3E500"));
        range2.setFrom(50);
        range2.setTo(100);

        Range range3 = new Range();
        range3.setColor(Color.parseColor("#ce0000"));
        range3.setFrom(100);
        range3.setTo(150);

        //add color ranges to gauge
        halfGauge.addRange(range);
        halfGauge.addRange(range2);
        halfGauge.addRange(range3);

        //set min max and current value
        halfGauge.setMinValue(0.0);
        halfGauge.setMaxValue(150.0);
        halfGauge.setValue(35.0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Air Quality Index");
        setSupportActionBar(toolbar);
        mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter(AirQualityActivity.this);

        //toolbar.setTitle("Air Quality Index");
        int value = 80;
        gauge1.setValue(value);
        textView1.setText(value + "/800");

        setHalfGauge();

        arrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandableView.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    expandableView.setVisibility(View.VISIBLE);
                    arrowBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }
                else
                {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    expandableView.setVisibility(View.GONE);
                    arrowBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
            }
        });

       /* expandableButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  expandabableLayout1.toggle();
            }
        });*/

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                startScanning(true);
            }
        });

        connectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothDevice != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    Intent gattServiceIntent = new Intent(AirQualityActivity.this, BluetoothLEService.class);
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                }
            }
        });

       /* connectService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNotifyCharacteristic != null) {
                    final int charaProp = mNotifyCharacteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        mBluetoothLEService.readCharacteristic(mNotifyCharacteristic);
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mBluetoothLEService.setCharacteristicNotification(mNotifyCharacteristic, true);
                    }
                }
            }
        });*/
        connectService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (BluetoothGattCharacteristic mNotifyCharacteristic: mNotifyCharacteristics) {

                    if (mNotifyCharacteristic != null) {
                        final int charaProp = mNotifyCharacteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            mBluetoothLEService.readCharacteristic(mNotifyCharacteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mBluetoothLEService.setCharacteristicNotification(mNotifyCharacteristic, true);
                        }
                    }
                }
            }
        });

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        }
        else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.REQUEST_BLUETOOTH_ENABLE_CODE);
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Your devices that don't support BLE", Toast.LENGTH_LONG).show();
            finish();
        }
        if (!mBluetoothAdapter.enable()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, Constants.REQUEST_BLUETOOTH_ENABLE_CODE);
        }
        registerReceiver(mGattUpdateReceiver, GattUpdateIntentFilter());
        if (mBluetoothLEService != null) {
            final boolean result = mBluetoothLEService.connect(bluetoothDevice.getAddress());
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLEService = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_BLUETOOTH_ENABLE_CODE && resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mBluetoothLEService = ((BluetoothLEService.LocalBinder) service).getService();
            if (!mBluetoothLEService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            mBluetoothLEService.connect(bluetoothDevice.getAddress());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mBluetoothLEService = null;
        }
    };

    private ScanCallback scanCallback = new ScanCallback() { // the result of the scanning
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            bluetoothDevice = result.getDevice();
            deviceAddress.setText(bluetoothDevice.getAddress());
            deviceName.setText(bluetoothDevice.getName());
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(TAG, "Scanning Failed " + errorCode);
            Toast.makeText(getApplicationContext(), "Scanning Failed", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    };

    private static IntentFilter GattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLEService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void startScanning(final boolean enable) {
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        Handler mHandler = new Handler();

        if (enable) {
            List<ScanFilter> scanFilters = new ArrayList<>();
            final ScanSettings settings = new ScanSettings.Builder().build();
            //ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(SampleGattAttributes.UUID_AIRQUALITY_SERVICE)).build();

            ScanFilter scanFilter = new ScanFilter.Builder().setDeviceAddress("00:A0:50:1A:D6:A3").build();

            scanFilters.add(scanFilter);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    progressBar.setVisibility(View.INVISIBLE);
                    bluetoothLeScanner.stopScan(scanCallback);
                }

            }, Constants.SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(scanFilters, settings, scanCallback);
           // bluetoothLeScanner.startScan(scanCallback);// mBluetoothAdapter.startLeScan(SampleGattAttributes.UUID_AIRQUALITY_SERVICE,);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }

    private void updateConnectionState(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceStatus.setText(status);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            if(!data.contains("%"))
                airQualityLevel.setText(data);
            else
                batteryLevelText.setText(data);
        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;

        String uuid = null;
        String serviceString = "unknown service";
        String charaString = "unknown characteristic";
        mNotifyCharacteristics = new ArrayList<BluetoothGattCharacteristic>();
        int charFound = 0;

        for (BluetoothGattService gattService : gattServices) {

            uuid = gattService.getUuid().toString();

            serviceString = SampleGattAttributes.lookup(uuid);

            if (serviceString != null) {
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();
                BluetoothGattCharacteristic gattCharacteristicFound = null;

                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    // HashMap<String, String> currentCharaData = new HashMap<String, String>();
                    uuid = gattCharacteristic.getUuid().toString();
                    charaString = SampleGattAttributes.lookup(uuid);
                    if (charaString != null) {
                        if(charFound == 0)
                            serviceName.setText(charaString);
                        else
                            serviceName.append("| " + charaString );
                        //serviceName.append(charaString);
                       // gattCharacteristicFound = gattCharacteristic;
                        mNotifyCharacteristics.add(gattCharacteristic);
                        charFound = 1;
                        //break;
                    }

                }
                //mNotifyCharacteristic = gattCharacteristicFound;
               // return;
            }
        }

    }


}
