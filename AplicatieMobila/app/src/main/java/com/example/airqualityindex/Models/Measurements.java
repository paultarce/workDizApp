package com.example.airqualityindex.Models;

public class Measurements {

    public long CO;
    public long SO2;
    public long NO2;
    public long O3;
    public long AQI;

    public String place;
    public long date; // timestamp

    public Measurements(long CO, long SO2, long NO2, long O3, long AQI, String place) {
        this.CO = CO;
        this.SO2 = SO2;
        this.NO2 = NO2;
        this.O3 = O3;
        this.AQI = AQI;
        this.place = place;
        this.date = System.currentTimeMillis();
    }
}
