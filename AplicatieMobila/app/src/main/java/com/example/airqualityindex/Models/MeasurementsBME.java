package com.example.airqualityindex.Models;

public class MeasurementsBME {

   public long rawTemp;
   public long rawHumid;
   public long rawPress;
   public double Temp;
   public double Humid;
   public double Press;
   public String place;
   public long date; // timestamp
    public String id;

    public MeasurementsBME(long rawTemp, long rawHumid, long rawPress, double temp, double humid, double press, String place) {
        this.rawTemp = rawTemp;
        this.rawHumid = rawHumid;
        this.rawPress = rawPress;
        this.Temp = temp;
        this.Humid = humid;
        this.Press = press;
        this.place = place;
        this.date = System.currentTimeMillis()/1000;
    }

    public MeasurementsBME()
    {

    }
}
