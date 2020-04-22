package com.example.airqualityindex;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
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
//import in.unicodelabs.kdgaugeview.KdGaugeView;
import pl.pawelkleczkowski.customgauge.CustomGauge;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import com.github.aakira.expandablelayout.ExpandableLayout;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.github.anastr.speedviewlib.TubeSpeedometer;
import com.github.anastr.speedviewlib.components.Section;
import com.google.android.material.button.MaterialButton;

import java.lang.Object;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AirQualityActivity extends AppCompatActivity { //sau  AppCompatActivity

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

    @BindView(R.id.expandableView)
    LinearLayout expandableView;

    @BindView(R.id.arrowBtn)
    Button arrowBtn;

    @BindView(R.id.cardView)
    CardView cardView;

   /* @BindView(R.id.gauge1)
    CustomGauge gauge1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textView1)
    TextView textView1;

    @BindView(R.id.halfGauge)
    HalfGauge halfGauge;

    @BindView(R.id.speedMeter)
    KdGaugeView speedView;

    @BindView(R.id.expandableLayout1)
    ExpandableLayout expandabableLayout1;

    @BindView(R.id.expandableButton1)
    Button expandableButton1;*/

    @BindView(R.id.gauge_AQI)
    TubeSpeedometer gauge_AQI;

    @BindView(R.id.gauge_CO)
    TubeSpeedometer gauge_CO;

    @BindView(R.id.gauge_NO2)
    TubeSpeedometer gauge_NO2;

    @BindView(R.id.gauge_O3)
    TubeSpeedometer gauge_O3;

    @BindView(R.id.gauge_PM10)
    TubeSpeedometer gauge_PM10;

    @BindView(R.id.gauge_PM25)
    TubeSpeedometer gauge_PM25;

    @BindView(R.id.gauge_SO2)
    TubeSpeedometer gauge_SO2;

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
                String [] bleValues = new String[2];
                bleValues = intent.getStringArrayExtra(BluetoothLEService.EXTRA_DATA);
                //displayData(intent.getStringExtra(BluetoothLEService.EXTRA_DATA));
                displayData(bleValues[0]);
                PushValueAndGetAQI(bleValues);
                //displayData(bleValues);
            }
        }
    };

    public void setHalfGauge()
    {
       /* Range range = new Range();
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
        halfGauge.setValue(35.0);*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // make app fullscreen
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); // bind all controls to this

        mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter(AirQualityActivity.this);

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Constants.REQUEST_BLUETOOTH_ENABLE_CODE);
        }

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Air Quality Index");
        setSupportActionBar(toolbar);


        //toolbar.setTitle("Air Quality Index");
       /* int value = 80;
        gauge1.setValue(value);
        textView1.setText(value + "/800");

       // setHalfGauge();
       // speedView.setSpeed(167);
       // speedView.drawSpeedometer(); */
        InitializeGauges();

        arrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandableView.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    expandableView.setVisibility(View.VISIBLE);
                   // arrowBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    ((MaterialButton)arrowBtn).setIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                }
                else
                {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    expandableView.setVisibility(View.GONE);
                   // arrowBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    ((MaterialButton)arrowBtn).setIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
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

        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                for(BluetoothDevice result: pairedDevices) {
                    if (result.getAddress().equals("B4:52:A9:01:8F:11")) {
                        bluetoothDevice = result;
                        deviceAddress.setText(bluetoothDevice.getAddress());
                        deviceName.setText(bluetoothDevice.getName());
                        progressBar.setVisibility(View.INVISIBLE);
                        return;
                    }
                }

            }
        });*/

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
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            mBluetoothLEService.readCharacteristic(mNotifyCharacteristic);
                            //add setCharacteristicNotification here too ???
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
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

        //Set<BluetoothDevice> bondedDevices =  mBluetoothAdapter.getBondedDevices(); // asta ??

        Handler mHandler = new Handler();

        if (enable) {
            List<ScanFilter> scanFilters = new ArrayList<>();
            final ScanSettings settings = new ScanSettings.Builder().build();
            //ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(SampleGattAttributes.UUID_AIRQUALITY_SERVICE)).build();

            //ScanFilter scanFilter = new ScanFilter.Builder().setDeviceAddress("00:A0:50:1A:D6:A3").build(); // Adresa MAC a modulului BLE
            ScanFilter scanFilter = new ScanFilter.Builder().setDeviceAddress("B4:52:A9:01:8F:11").build(); // Adresa MAC a modulului BLE

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

    long aqi_CO = 0; long aqi_SO2 = 0; long aqi_O3 = 0; long aqi_PM25 = 0; long aqi_PM10 = 0; long aqi_NO2 = 0;
    long AQI_VALUE = 0;
    private void PushValueAndGetAQI(String[] data)
    {
        if (data[0] != null && data[1] != null)
        {
            long subIndexValue = 0;
            switch(data[1])
            {
                case "CO" :
                    subIndexValue = AqiUtils.GetSubIndexValue_CO(Double.parseDouble(data[0]));
                    //subIndexValue = Math.round(Double.parseDouble(data[0]));
                    gauge_CO.speedTo(subIndexValue);
                    break;
                case "SO2":
                    break;
                default:
                    break;
            }

            if(subIndexValue > aqi_CO && subIndexValue > aqi_SO2 && subIndexValue > aqi_O3 && subIndexValue > aqi_PM25
                    && subIndexValue > aqi_PM10 && subIndexValue > aqi_NO2 )
            {
                AQI_VALUE = subIndexValue;
                gauge_AQI.speedTo(subIndexValue);
                gauge_AQI.setUnit(data[1]); // display the Main Pollutant
            }
        }
    }
    private void displayData(String[] data)
    {
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


   /* private ArrayList<Section> GetSections()
    {
        ArrayList<Section> sections = new ArrayList<Section>();
        //Section s
    }*/

    public void InitializeGauges()
    {
        List<Section> sectionsAQI = new ArrayList<Section>();
        Section s1 = new Section(0f,.1f, Color.rgb(34, 139, 34)); s1.setStyle(Section.Style.SQUARE); sectionsAQI.add(s1);
        Section s2 = new Section(.1f, .2f, Color.rgb(255,222,0)); s2.setStyle(Section.Style.SQUARE); sectionsAQI.add(s2);
        Section s3 = new Section(.2f,.3f, Color.rgb(255,165, 0)); s3.setStyle(Section.Style.SQUARE); sectionsAQI.add(s3);
        Section s4 = new Section(.3f, .4f, Color.RED); s4.setStyle(Section.Style.SQUARE);  sectionsAQI.add(s4);
        Section s5 = new Section(.4f, .6f, Color.rgb(128, 0, 128)); s5.setStyle(Section.Style.SQUARE); sectionsAQI.add(s5);
        Section s6 = new Section(.6f, 1f, Color.rgb(0,0,0)); s6.setStyle(Section.Style.SQUARE); sectionsAQI.add(s6);

        gauge_AQI.setMaxSpeed(500);
        gauge_AQI.setTrembleData(0,0);
        gauge_AQI.clearSections();
        gauge_AQI.addSections(sectionsAQI);
        gauge_AQI.speedTo(120);
        /*List<Section> sections = gauge_AQI.getSections();
        sections.get(0).setColor(Color.GREEN);
        sections.get(1).setColor(Color.YELLOW);
        sections.get(2).setColor(Color.rgb(255,165, 0));// ORANGE
        sections.get(3).setColor(Color.RED);
        sections.get(4).setColor(Color.rgb(128,0,0));
        //gauge_AQI.addSections(sections);*/


        gauge_NO2.setMaxSpeed(500);
        gauge_NO2.setTrembleData(0,0);
        gauge_NO2.clearSections();
        gauge_NO2.addSections(sectionsAQI);
        gauge_NO2.speedTo(49);


        gauge_PM10.setMaxSpeed(500);
        gauge_PM10.setTrembleData(0,0);
        gauge_PM10.clearSections();
        gauge_PM10.addSections(sectionsAQI);
        gauge_PM10.speedTo(75);


        gauge_PM25.setMaxSpeed(500);
        gauge_PM25.setTrembleData(0,0);
        gauge_PM25.clearSections();
        gauge_PM25.addSections(sectionsAQI);
        gauge_PM25.speedTo(101);

        gauge_CO.setMaxSpeed(500);
        gauge_CO.setTrembleData(0,0);
        gauge_CO.clearSections();
        gauge_CO.addSections(sectionsAQI);
        gauge_CO.speedTo(151);


        gauge_O3.setMaxSpeed(500);
        gauge_O3.setTrembleData(0,0);
        gauge_O3.clearSections();
        gauge_O3.addSections(sectionsAQI);
        gauge_O3.speedTo(201);

        gauge_SO2.setMaxSpeed(500);
        gauge_SO2.setTrembleData(0,0);
        gauge_SO2.clearSections();
        gauge_SO2.addSections(sectionsAQI);
        gauge_SO2.speedTo(301);
    }
}
