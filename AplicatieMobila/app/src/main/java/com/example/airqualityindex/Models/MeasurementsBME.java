package com.example.airqualityindex.Models;

public class MeasurementsBME {

   public long rawTemp;
   public long rawHumid;
   public long rawPress;
   public float Temp;
   public float Humid;
   public float Press;
   public String place;
   public long date; // timestamp
    public String id;
    public String device;

    public MeasurementsBME(long rawTemp, long rawHumid, long rawPress, float temp, float humid, float press, String place, String device) {
        this.rawTemp = rawTemp;
        this.rawHumid = rawHumid;
        this.rawPress = rawPress;
        this.Temp =  temp;
        this.Humid = humid;
        this.Press = press;
        this.place = place;
        this.device = device;
        this.date = System.currentTimeMillis()/1000;
    }

    public MeasurementsBME()
    {

    }
}
