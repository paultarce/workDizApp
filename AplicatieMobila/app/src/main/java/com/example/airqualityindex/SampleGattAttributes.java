package com.example.airqualityindex;

import java.util.HashMap;

public class SampleGattAttributes {


    //TO BE Used :
    public static String SERVICE_SENSORS_UUID = "26d2a5e0-7b2c-11e6-8b77-86f30ca893d3";
    public static String CHARACTERISTIC_BME_DATA_UUID = "26d2a5e1-7b2c-11e6-8b77-86f30ca893d3"; //READ
    public static String CHARACTERISTIC_PMS_DATA_UUID = "26d2a5e2-7b2c-11e6-8b77-86f30ca893d3"; //READ
    public static String CHARACTERISTIC_SPEC_DATA_UUID = "26d2a5e3-7b2c-11e6-8b77-86f30ca893d3"; //READ
    public static String CHARACTERISTIC_SENSORS_DATA_UUID = "26d2a5e4-7b2c-11e6-8b77-86f30ca893d3"; //NOTIFY
    public static String CHARACTERISTIC_DEVICE_CONFIG_UUID = "26d2a5e5-7b2c-11e6-8b77-86f30ca893d3"; //WRITE
    public static String CHARACTERISTIC_TIMESTAMP_UUID = "26d2a5e6-7b2c-11e6-8b77-86f30ca893d3"; //READ
    public static String CHARACTERISTIC_DEVICE_INFO_UUID = "26d2a5e7-7b2c-11e6-8b77-86f30ca893d3"; //READ



    //Battery level
    public static final String UUID_BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb"; // serviciu
    public static final String UUID_BATTERY_LEVEL_UUID = "00002a19-0000-1000-8000-00805f9b34fb"; //caracteristica
    //CELE 2 de mai JOS
   /* public static final String UUID_AIRQUALITY_SERVICE = "26d2a5e0-7b2c-11e6-8b77-86f30ca893d3"; // serviciu
    public static final String UUID_AIRQUALITY_LEVEL_UUID = "26d2a5e4-7b2c-11e6-8b77-86f30ca893d3"; //caracteristica*/

    public static final String UUID_AIRQUALITY_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb"; // serviciu
    public static final String UUID_AIRQUALITY_LEVEL_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb"; //caracteristica

    private static HashMap<String, String> attributes = new HashMap();

    static{
        attributes.put(UUID_AIRQUALITY_LEVEL_UUID, "AirQuality level");
        attributes.put(UUID_AIRQUALITY_SERVICE, "AirQuality Service");

        attributes.put(UUID_BATTERY_LEVEL_UUID, "Battery level");
        attributes.put(UUID_BATTERY_SERVICE, "Battery Service");
     //   attributes.put(UUID_BATTERY_LEVEL_UUID, "Battery level");
    }

    public static String lookup(String uuid)
    {
        String name = attributes.get(uuid);
        return name;
    }

}
