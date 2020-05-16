package com.example.airqualityindex.Models;

public class Measurements {

    public long rawCO;
    public long rawSO2;
    public long rawNO2;
    public long rawO3;

    public long CO;
    public long SO2;
    public long NO2;
    public long O3;
    public long AQI;

    public String place;
    public long date; // timestamp
    public String id;

    public Measurements()
    {

    }

    public Measurements(long rawCO, long rawSO2, long rawNO2, long rawO3,long CO, long SO2, long NO2, long O3, long AQI, String place) {
        this.rawCO = rawCO;
        this.rawNO2 = rawSO2;
        this.rawSO2 = rawSO2;
        this.rawO3 = rawO3;
        this.CO = CO;
        this.SO2 = SO2;
        this.NO2 = NO2;
        this.O3 = O3;
        this.AQI = AQI;
        this.place = place;
        this.date = System.currentTimeMillis() / 1000;
    }
}
