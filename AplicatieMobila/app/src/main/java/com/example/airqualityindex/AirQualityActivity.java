package com.example.airqualityindex;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
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

import androidx.annotation.NonNull;
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
import com.example.airqualityindex.Models.Measurements;
import com.github.anastr.speedviewlib.TubeSpeedometer;
import com.github.anastr.speedviewlib.components.Section;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.Object;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AirQualityActivity extends AppCompatActivity { //sau  AppCompatActivity

    private static final String TAG = "MainActivity";

    @BindView(R.id.startScan)
    Button startScan;

    @BindView(R.id.connectDevice)
    Button connectDevice;

    @BindView(R.id.getPairedDevice)
    Button getPairedDevice;

    @BindView(R.id.disconnectDevice)
    Button disconnectDevice;

    @BindView(R.id.readDBbutton)
    Button readDBbutton;

    @BindView(R.id.btnShowCharts)
    Button btnShowCharts;

    @BindView(R.id.btnClearDB)
    Button btnClearDB;

    @BindView(R.id.btnSetSaveRate)
    Button btnSetSaveRate;

    @BindView(R.id.txtInputSaveDataRate)
    TextInputLayout txtInputSaveDataRate;

    @BindView(R.id.deviceState)
    TextView deviceStatus;

    @BindView(R.id.txtRawValueCO)
    TextView txtRawValueCO;

    @BindView(R.id.txtRawValueNO2)
    TextView txtRawValueNO2;

    @BindView(R.id.txtRawValueSO2)
    TextView txtRawValueSO2;

    @BindView(R.id.txtRawValueO3)
    TextView txtRawValueO3;

    @BindView(R.id.txtRawValueTemp)
    TextView txtRawValueTemp;

    @BindView(R.id.txtRawValueHumid)
    TextView txtRawValueHumid;

    @BindView(R.id.txtRawValuePressure)
    TextView txtRawValuePressure;

    @BindView(R.id.batteryLevel)
    TextView batteryLevelText;

    @BindView(R.id.deviceAddress)
    TextView deviceAddress;
    /*@BindView(R.id.deviceName)
    TextView deviceName;*/
    @BindView(R.id.serviceName)
    TextView serviceName;

    @BindView(R.id.txtNrMeasurements)
    TextView txtNrMeasurements;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.switchSaveDB)
    Switch switchSaveDB;

    @BindView(R.id.expandableView)
    LinearLayout expandableView;

    @BindView(R.id.arrowBtn)
    Button arrowBtn;

    @BindView(R.id.spinnerChartInterval)
    Spinner spinnerChartInterval;

    @BindView(R.id.spinnerBoundedDevices)
    Spinner spinnerBoundedDevices;

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

    @BindView(R.id.chartCO)
    LineChart chartCO;
    @BindView(R.id.chartSO2)
    LineChart chartSO2;
    @BindView(R.id.chartO3)
    LineChart chartO3;
    @BindView(R.id.chartNO2)
    LineChart chartNO2;

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

    Set<BluetoothDevice> pairedDevices;
    ArrayList<BluetoothDevice> scannedDevices;

    private DatabaseReference  databaseAQI;
    ArrayList<Measurements> measurementsDB;
    Handler readHandler = new Handler();
    int saveToDbRate;
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
                String [] bleValues = new String[5];
                bleValues = intent.getStringArrayExtra(BluetoothLEService.EXTRA_DATA);
                //displayData(intent.getStringExtra(BluetoothLEService.EXTRA_DATA));
                if(bleValues[0].equals("BATTERY"))
                {
                    displayData(bleValues);
                }
                else if(bleValues[0].equals("SensorValues"))
                {
                    txtRawValueCO.setText(bleValues[1]);
                    txtRawValueNO2.setText(bleValues[2]);
                    txtRawValueSO2.setText(bleValues[3]);
                    txtRawValueO3.setText(bleValues[4]);
                    PushValueAndGetAQI(bleValues);
                }
                else if(bleValues[0].equals("SensorValuesBME"))
                {
                    txtRawValueTemp.setText(bleValues[1]);
                    txtRawValueHumid.setText(bleValues[2]);
                    txtRawValuePressure.setText(bleValues[3]);
                }
            }
        }
    };

    int allCharReady = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // make app fullscreen
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); // bind all controls to this

        mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter(AirQualityActivity.this);

        InitializeSpinner();


        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Constants.REQUEST_BLUETOOTH_ENABLE_CODE);
        }

        connectDevice.setEnabled(false);
        connectService.setEnabled(false);
        disconnectDevice.setEnabled(false);
        btnShowCharts.setEnabled(false);

        //FIREBASE -- https://airqualityindex-d1fd2.firebaseio.com/
        databaseAQI = FirebaseDatabase.getInstance().getReference("measurements");
        //FirebaseDatabase.getInstance().getReference().child("measurements").child("CO").setValue("3.4");
        measurementsDB = new ArrayList<>();
        scannedDevices = new ArrayList<>();
        InitializeGauges();
        saveToDbRate = 20;

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

        startScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                startScanning(true);
            }
        });

        getPairedDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scannedDevices.clear();
                pairedDevices = mBluetoothAdapter.getBondedDevices();
                /*for(BluetoothDevice result: pairedDevices) {
                    if (result.getAddress().equals("00:A0:50:1A:D6:A3")) {
                        bluetoothDevice = result;
                        deviceAddress.setText(bluetoothDevice.getAddress());
                       // deviceName.setText(bluetoothDevice.getName());
                        progressBar.setVisibility(View.INVISIBLE);
                        connectDevice.setEnabled(true);
                        return;
                    }
                }*/
                if(pairedDevices.size() > 0) {
                    ArrayList<String> pairedDevicesNames = new ArrayList<>();
                    for (BluetoothDevice device : pairedDevices) {
                        pairedDevicesNames.add(device.getName());
                    }
                    ArrayAdapter<String> adapterBoundedDevices = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, pairedDevicesNames);
                    adapterBoundedDevices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBoundedDevices.setAdapter(adapterBoundedDevices);

                    //set the first element as the device to connect to
                    spinnerBoundedDevices.setSelection(0);
                  /*  bluetoothDevice = pairedDevices.iterator().next();
                    deviceAddress.setText(bluetoothDevice.getAddress());
                    progressBar.setVisibility(View.INVISIBLE);
                    connectDevice.setEnabled(true);*/
                }
                else
                    Toast.makeText(getApplicationContext(), "No Paired Devices Available", Toast.LENGTH_SHORT ).show();
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

        disconnectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readHandler.removeCallbacks(runnableCode);
                allCharReady = 0;
                mBluetoothLEService.disconnect();
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

                Toast.makeText(AirQualityActivity.this, "Connecting to service...", Toast.LENGTH_LONG).show();
                for (BluetoothGattCharacteristic mNotifyCharacteristic: mNotifyCharacteristics) {

                    if (mNotifyCharacteristic != null) {
                        final int charaProp = mNotifyCharacteristic.getProperties();
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            //mBluetoothLEService.readCharacteristic(mNotifyCharacteristic);
                            //add setCharacteristicNotification here too ???
                            if(mNotifyCharacteristic.getUuid().toString().equals(SampleGattAttributes.CHARACTERISTIC_SPEC_DATA_UUID))
                            {
                                specDataCharacteristic = mNotifyCharacteristic;
                                allCharReady ++;
                                //readHandler.post(runnableCode);
                            }
                            if(mNotifyCharacteristic.getUuid().toString().equals(SampleGattAttributes.CHARACTERISTIC_BME_DATA_UUID))
                            {
                                bmeDataCharacteristic = mNotifyCharacteristic;
                                allCharReady ++;
                                //readHandler.post(runnableCode);
                            }
                            if(allCharReady == 2)
                                readHandler.post(runnableCode);
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mBluetoothLEService.setCharacteristicNotification(mNotifyCharacteristic, true);
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0){
                            mBluetoothLEService.writeCharacteristic(mNotifyCharacteristic); // naming not
                        }
                    }
                }
            }
        });

        readDBbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAQI.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        measurementsDB.clear();
                            for(DataSnapshot measurementsSnapshot : dataSnapshot.getChildren()){
                                Measurements meas = measurementsSnapshot.getValue(Measurements.class);
                                measurementsDB.add(meas);
                                txtNrMeasurements.setText("DB Size:" + String.valueOf(measurementsDB.size()));
                            }
                            btnShowCharts.setEnabled(true);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                            int a = 2;
                    }
                });
            }
        });

        btnClearDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // DatabaseReference refMeasurements = FirebaseDatabase.getInstance(
                databaseAQI.removeValue();
            }
        });

        btnShowCharts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String intervalText = spinnerChartInterval.getSelectedItem().toString();
                if(measurementsDB.size() > 0)
                {
                    int intervalSeconds = AqiUtils.GetSecondsFromSpinner(intervalText);

                    ArrayList<Measurements> intervalMeasurements = AqiUtils.GetTimeRelatedMeasurement(measurementsDB, intervalSeconds);

                    if(intervalMeasurements.size() > 0 ) {
                        List<Long> CO_values = new ArrayList<>();
                        List<Long> NO2_values = new ArrayList<>();
                        List<Long> SO2_values = new ArrayList<>();
                        List<Long> timeAgo_SO2 = new ArrayList<>();
                        List<Long> O3_values = new ArrayList<>();
                        long initialTimeAgo = (System.currentTimeMillis() / 1000) - intervalMeasurements.get(0).date; // in seconds
                        for (Measurements measure : intervalMeasurements) {
                            CO_values.add(measure.CO);
                            NO2_values.add(measure.NO2);
                            SO2_values.add(measure.SO2);
                            O3_values.add(measure.O3);
                            timeAgo_SO2.add(initialTimeAgo);
                            initialTimeAgo -= 20;
                        }

                        DrawLineChart_CO(CO_values, intervalSeconds);
                        DrawLineChart_SO2(SO2_values, timeAgo_SO2);
                        DrawLineChart_O3(O3_values, intervalSeconds);
                        DrawLineChart_NO2(NO2_values, intervalSeconds);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"No Data in this time slot!", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No Measurements in DB!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSetSaveRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDbRate = Integer.parseInt(txtInputSaveDataRate.getEditText().getText().toString());
            }
        });

        spinnerBoundedDevices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String deviceName = parent.getItemAtPosition(position).toString();

                if(scannedDevices.size() > 0)
                {
                    for(BluetoothDevice deviceBonded : scannedDevices)
                    {
                        if(deviceBonded.getName().equals(deviceName))
                        {
                            bluetoothDevice = deviceBonded;
                            deviceAddress.setText(bluetoothDevice.getAddress());
                            // deviceName.setText(bluetoothDevice.getName());
                            progressBar.setVisibility(View.INVISIBLE);
                            connectDevice.setEnabled(true);
                            return;
                        }
                    }
                }
                else {
                    for (BluetoothDevice deviceBonded : pairedDevices) {
                        if (deviceBonded.getName().equals(deviceName)) {
                            bluetoothDevice = deviceBonded;
                            deviceAddress.setText(bluetoothDevice.getAddress());
                            // deviceName.setText(bluetoothDevice.getName());
                            progressBar.setVisibility(View.INVISIBLE);
                            connectDevice.setEnabled(true);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    BluetoothGattCharacteristic specDataCharacteristic;
    BluetoothGattCharacteristic bmeDataCharacteristic;

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
            if(!scannedDevices.contains(bluetoothDevice))
                scannedDevices.add(bluetoothDevice);
            //deviceAddress.setText(bluetoothDevice.getAddress());
           // deviceName.setText(bluetoothDevice.getName());
            //progressBar.setVisibility(View.INVISIBLE);
            //connectDevice.setEnabled(true);
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
        scannedDevices.clear();

        if (enable) {
            List<ScanFilter> scanFilters = new ArrayList<>();
            final ScanSettings settings = new ScanSettings.Builder().build();
            //ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(SampleGattAttributes.UUID_AIRQUALITY_SERVICE)).build();

            ScanFilter scanFilter = new ScanFilter.Builder().setDeviceAddress("00:A0:50:1A:D6:A3").build(); // Adresa MAC a modulului BLE
            //ScanFilter scanFilter = new ScanFilter.Builder().setDeviceAddress("B4:52:A9:01:8F:11").build(); // Adresa MAC a modulului BLE

            scanFilters.add(scanFilter);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //scanning finised
                    mScanning = false;
                    progressBar.setVisibility(View.INVISIBLE);
                    bluetoothLeScanner.stopScan(scanCallback);


                    if(scannedDevices.size() > 0)
                    {
                        ArrayList<String> scannedDevicesNames = new ArrayList<>();
                        for (BluetoothDevice device : scannedDevices) {
                            scannedDevicesNames.add(device.getName());
                        }
                        ArrayAdapter<String> adapterBoundedDevices = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, scannedDevicesNames);
                        adapterBoundedDevices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerBoundedDevices.setAdapter(adapterBoundedDevices);

                        //set the first element as the device to connect to
                        spinnerBoundedDevices.setSelection(0);
                        connectDevice.setEnabled(true);
                    }
                }

            }, Constants.SCAN_PERIOD);

            mScanning = true;
           // bluetoothLeScanner.startScan(scanFilters, settings, scanCallback);
            bluetoothLeScanner.startScan(scanCallback);// mBluetoothAdapter.startLeScan(SampleGattAttributes.UUID_AIRQUALITY_SERVICE,);
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
                if(status == "connected") {
                    disconnectDevice.setEnabled(true);
                    connectDevice.setEnabled(false);
                    connectService.setEnabled(true);
                }
                else{
                    readHandler.removeCallbacks(runnableCode);
                    allCharReady = 0;
                    disconnectDevice.setEnabled(false);
                    connectDevice.setEnabled(true);
                    connectService.setEnabled(false);
                }
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            if(!data.contains("%"))
                txtRawValueCO.setText(data);
            else
                batteryLevelText.setText(data);
        }
    }

    long aqi_CO = 0; long aqi_SO2 = 0; long aqi_O3 = 0; long aqi_PM25 = 0; long aqi_PM10 = 0; long aqi_NO2 = 0;
    long AQI_VALUE = 0;

    private void PushValueAndGetAQI(String[] data)
    {
        long subIndexValue = 0;
        Pair<Long, Double> subIndexAndConcentration_CO = AqiUtils.GetSubIndexValue_CO(Double.parseDouble(data[1]));
        gauge_CO.speedTo(subIndexAndConcentration_CO.first);
        gauge_CO.setUnit(subIndexAndConcentration_CO.second +"ppm");

        Pair<Long, Double> subIndexAndConcentration_NO2 = AqiUtils.GetSubIndexValue_NO2(Double.parseDouble(data[2]));
        gauge_NO2.speedTo(subIndexAndConcentration_NO2.first);
        gauge_NO2.setUnit(subIndexAndConcentration_NO2.second + "ppb");

        Pair<Long, Double> subIndexAndConcentration_SO2 = AqiUtils.GetSubIndexValue_SO2(Double.parseDouble(data[3]));
        gauge_SO2.speedTo(subIndexAndConcentration_SO2.first);
        gauge_SO2.setUnit(subIndexAndConcentration_SO2.second + "ppb");

        Pair<Long, Double> subIndexAndConcentration_O3 = AqiUtils.GetSubIndexValue_O3(Double.parseDouble(data[4]));
        gauge_O3.speedTo(subIndexAndConcentration_O3.first);
        gauge_O3.setUnit(subIndexAndConcentration_O3.second + "ppb");

        //firs - SubIndexValue    second - concentration in ppb or ppm (CO)
        long subIndexValue_CO = subIndexAndConcentration_CO.first;
        long subIndexValue_NO2 = subIndexAndConcentration_NO2.first;
        long subIndexValue_SO2 = subIndexAndConcentration_SO2.first;
        long subIndexValue_O3 = subIndexAndConcentration_O3.first;

        if(subIndexValue_CO > subIndexValue_O3 && subIndexValue_CO > subIndexValue_NO2 && subIndexValue_CO > subIndexValue_SO2)
        {
            AQI_VALUE = subIndexValue_CO;
            gauge_AQI.speedTo(subIndexValue_CO);
            gauge_AQI.setUnit("CO"); // display the Main Pollutant
        }
        if(subIndexValue_NO2 > subIndexValue_O3 && subIndexValue_NO2 > subIndexValue_NO2 && subIndexValue_NO2 > subIndexValue_CO)
        {
            AQI_VALUE = subIndexValue_NO2;
            gauge_AQI.speedTo(subIndexValue_NO2);
            gauge_AQI.setUnit("NO2"); // display the Main Pollutant
        }
        if(subIndexValue_SO2 > subIndexValue_O3 && subIndexValue_SO2 > subIndexValue_NO2 && subIndexValue_SO2 > subIndexValue_CO)
        {
            AQI_VALUE = subIndexValue_SO2;
            gauge_AQI.speedTo(subIndexValue_SO2);
            gauge_AQI.setUnit("SO2"); // display the Main Pollutant
        }
        if(subIndexValue_O3 > subIndexValue_NO2 && subIndexValue_O3 > subIndexValue_CO && subIndexValue_O3 > subIndexValue_SO2)
        {
            AQI_VALUE = subIndexValue_O3;
            gauge_AQI.speedTo(subIndexValue_O3);
            gauge_AQI.setUnit("O3"); // display the Main Pollutant
        }

        if(switchSaveDB.isChecked()) {
            Measurements measurement = new Measurements(Long.parseLong(data[1]), Long.parseLong(data[3]), Long.parseLong(data[2]), Long.parseLong(data[4]),  subIndexValue_CO, subIndexValue_SO2, subIndexValue_NO2, subIndexValue_O3, AQI_VALUE, "HOME");
            // Save to FIREBASE DB
            AqiUtils.SaveMeasurementToDatabase(databaseAQI, measurement);
            Toast.makeText(this, "Measurement added", Toast.LENGTH_SHORT).show();
        }
       /* if (data[0] != null && data[1] != null)
        {
            long subIndexValue = 0;
            switch(data[0])
            {
                case "CO" :
                    subIndexValue = AqiUtils.GetSubIndexValue_CO(Double.parseDouble(data[1]));
                    //subIndexValue = Math.round(Double.parseDouble(data[0]));
                    gauge_CO.speedTo(subIndexValue);
                    break;
                case "SO2":
                    break;
                case "NO2":
                    break;
                case "O3":
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
        }*/
    }
    private void displayData(String[] data)
    {
        if (data[0] != null && data[1] != null) {
            long subIndexValue = 0;
            switch (data[0]) {
                case "CO":
                    //subIndexValue = AqiUtils.GetSubIndexValue_CO(Double.parseDouble(data[0]));
                    //subIndexValue = Math.round(Double.parseDouble(data[0]));
                    gauge_CO.speedTo(subIndexValue);
                    txtRawValueCO.setText(data[0]);
                    break;
                case "BATTERY":
                    batteryLevelText.setText(data[1] + "%");
                    break;
                default:
                    break;
            }
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
                        connectService.setEnabled(true);
                        //break;
                    }

                }
                //mNotifyCharacteristic = gattCharacteristicFound;
               // return;
            }
        }
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            //do something here
            mBluetoothLEService.readCharacteristic(bmeDataCharacteristic);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mBluetoothLEService.readCharacteristic(specDataCharacteristic);
            readHandler.postDelayed(this, saveToDbRate * 1000);
        }
    };

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

    public void InitializeSpinner()
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinnerChartIntervalArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChartInterval.setAdapter(adapter);
    }

    public void DrawLineChart_CO(List<Long> CO_values, int seconds)
    {
        Description desc = new Description();
        desc.setText("CO Chart");
        chartCO.setDescription(desc);

        //Y axis
        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 0; i < CO_values.size(); i++)
        {
            yData.add(new Entry(i,CO_values.get(i)));
        }
        LineDataSet lineDataSet1 = new LineDataSet(yData, "CO Values");
        lineDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);

        //
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        chartCO.setData(data);
        chartCO.invalidate();

        //X axis
        ArrayList<String> xData = new ArrayList<>();
        for(int i = 1; i < 12; i++ ) // din 5 in 5 minute
        {
            xData.add(String.valueOf(5*i));
        }
    }

    public void DrawLineChart_NO2(List<Long> NO_values, int seconds)
    {
        Description desc = new Description();
        desc.setText("NO2 Chart");
        chartNO2.setDescription(desc);

        //Y axis
        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 0; i < NO_values.size(); i++)
        {
            yData.add(new Entry(i, NO_values.get(i)));
        }
        LineDataSet lineDataSet1 = new LineDataSet(yData, "NO2 Values");
        lineDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);

        //
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        chartNO2.setData(data);
        chartNO2.invalidate();
    }

    public void DrawLineChart_SO2(List<Long> SO_values, List<Long> timeAgo_SO2)
    {
        Description desc = new Description();
        desc.setText("SO2 Chart");
        chartSO2.setDescription(desc);

        //Y axis
        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 0; i < SO_values.size(); i++)
        {
            yData.add(new Entry(timeAgo_SO2.get(timeAgo_SO2.size() - 1 - i),SO_values.get(i)));
        }

        XAxis xAx = chartSO2.getXAxis();
        xAx.setAxisMaximum(timeAgo_SO2.get(0));
        xAx.setAxisMinimum(timeAgo_SO2.get(timeAgo_SO2.size() -  1));

        /*YAxis yAx = chartSO2.getAxisLeft();
        YAxis yAxR = chartSO2.getAxisRight();
        yAx.setAxisMinimum(0);
        yAx.setAxisMaximum(300);

        yAxR.setAxisMinimum(0);
        yAxR.setAxisMaximum(300);*/

        LineDataSet lineDataSet1 = new LineDataSet(yData, "SO2 Values");
        lineDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);

        //
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);


        LineData data = new LineData(dataSets);
        chartSO2.setData(data);
        chartSO2.invalidate();


    }

    public void DrawLineChart_O3(List<Long> O3_values, int seconds)
    {
        Description desc = new Description();
        desc.setText("O3 Chart");
        chartO3.setDescription(desc);

        //Y axis
        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 0; i < O3_values.size(); i++)
        {
            yData.add(new Entry(i,O3_values.get(i)));
        }
        LineDataSet lineDataSet1 = new LineDataSet(yData, "O3 Values");
        lineDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        chartO3.setData(data);
        chartO3.invalidate();
    }


}
