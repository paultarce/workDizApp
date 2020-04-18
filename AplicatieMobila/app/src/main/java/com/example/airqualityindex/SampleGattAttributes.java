package com.example.airqualityindex;

import java.util.HashMap;

public class SampleGattAttributes {

    //public static final String UUID_AIRQUALITY_SERVICE = "00001800-0000-1000-8000-00805f9b34fb";
    //public static final String UUID_AIRQUALITY_SERVICE = "00002a00-0000-1000-8000-00805f9b34fb";
    //public static final String UUID_AIRQUALITY_SERVICE = "b9404000-f5f8-466e-aff9-25556b57fe6d"; // serviciu
    //public static final String UUID_AIRQUALITY_LEVEL_UUID = "b9404001-f5f8-466e-aff9-25556b57fe6d"; //caracteristica

   // public static final String UUID_AIRQUALITY_LEVEL_UUID = "00002a19-0000-1000-8000-00805f9b34fb";
    //public static final String UUID_AIRQUALITY_LEVEL_UUID = "2A19";
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
