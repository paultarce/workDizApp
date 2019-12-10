package com.example.airqualityindex;

import java.util.HashMap;

public class SampleGattAttributes {

    public static final String UUID_AIRQUALITY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public static final String UUID_AIRQUALITY_LEVEL_UUID = "00002a19-0000-1000-8000-00805f9b34fb";

    private static HashMap<String, String> attributes = new HashMap();

    static{
        attributes.put(UUID_AIRQUALITY_LEVEL_UUID, "AirQuality level");
        attributes.put(UUID_AIRQUALITY_SERVICE, "Battery Service");
    }

    public static String lookup(String uuid)
    {
        String name = attributes.get(uuid);
        return name;
    }
}
