package com.example.airqualityindex;

import java.util.HashMap;

public class SampleGattAttributes {

    //public static final String UUID_AIRQUALITY_SERVICE = "00001800-0000-1000-8000-00805f9b34fb";
    //public static final String UUID_AIRQUALITY_SERVICE = "00002a00-0000-1000-8000-00805f9b34fb";
    public static final String UUID_AIRQUALITY_SERVICE = "b9404000-f5f8-466e-aff9-25556b57fe6d"; // serviciu
    public static final String UUID_AIRQUALITY_LEVEL_UUID = "b9404001-f5f8-466e-aff9-25556b57fe6d"; //caracteristica

    private static HashMap<String, String> attributes = new HashMap();

    static{
        attributes.put(UUID_AIRQUALITY_LEVEL_UUID, "AirQuality level");
        attributes.put(UUID_AIRQUALITY_SERVICE, "AirQuality Service");
    }

    public static String lookup(String uuid)
    {
        String name = attributes.get(uuid);
        return name;
    }

}
