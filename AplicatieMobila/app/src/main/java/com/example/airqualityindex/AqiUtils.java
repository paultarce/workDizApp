package com.example.airqualityindex;
import android.util.Pair;
import android.widget.Toast;

import com.example.airqualityindex.Models.Measurements;
import com.example.airqualityindex.Models.MeasurementsBME;
import com.google.firebase.database.DatabaseReference;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AqiUtils {

    //https://www3.epa.gov/airnow/aqi-technical-assistance-document-sept2018.pdf
    //inputRawValue - value of pollutant in ug/m3
    /* CO - carbon monoxide
     GOOD( 0 - 50 )               <=>    0    -    5038   ug/m3 :  0 - 4.4 ppm
     MODERATE(51-100)             <=>  5038   -    10.736       :   4.5 - 9.4 ppm
     Unhealthy_sensitive(101-150) <=>  10.736 -    14.198       :   9.5 - 12.4
     Unhealthy ( 151 - 200 )      <=>  14.198 -    17.633
     Very Unhealthy(201 - 300)    <=>  17.633 -    34.808
     Hazardous( 301 - 500 )       <=>  34.808 -    57.708
    */
    public static Pair<Long, Double> GetSubIndexValue_CO(double inputRawValue) {
        double subIndexValue_double = 0;
        long subIndexValue = 0;
        double sensitivity = 6.1; // 6.1 ppm,  0.00611 nA/ppb;

        double measuredVoltage = GetVoltageFromQuanta(inputRawValue); // V

        double nanoAmphereVal = ((measuredVoltage - 0.5) / 350000) * 1000000000;// (* 10 ^ 9)
        double aqiValuePpm = nanoAmphereVal / sensitivity;
        /*if(inputRawValue < 5038)
            subIndexValue_double = 0.00992 * (inputRawValue - 0) + 0; //  (double)(50 - 0) / (5038 - 0))
        else if(inputRawValue < 10736)
            subIndexValue_double = ((100 - 51) / (10736 - 5039)) * (inputRawValue - 5039) + 51;
        else if(inputRawValue < 14198)
            subIndexValue_double = ((150 - 101) / (14198 - 10737)) * (inputRawValue - 10737) + 101;
        else if(inputRawValue < 17633)
            subIndexValue_double = ((200 - 151) / (17633 - 14199)) * (inputRawValue - 14199) + 151;
        else if(inputRawValue < 34808)
            subIndexValue_double = ((300 - 201) / (34808 - 17634)) * (inputRawValue - 17634) + 201;
        else if(inputRawValue < 57708)
            subIndexValue_double = ((500 - 301) / (57708 - 34809)) * (inputRawValue - 34809) + 301;*/
        //inputRawValue = inputRawValue / 1000; // Transform to ppm - as the standard is for CO
        if (aqiValuePpm <= 0)
            subIndexValue_double = 0;
        else if (aqiValuePpm <= 4.4)
            subIndexValue_double = ((double) 50 / 4.4) * (aqiValuePpm - 0) + 0;
        else if (aqiValuePpm <= 9.4)
            subIndexValue_double = ((double) 49 / 4.9) * (aqiValuePpm - 4.5) + 51;
        else if (aqiValuePpm <= 12.4)
            subIndexValue_double = ((double) 49 / 2.9) * (aqiValuePpm - 9.5) + 101;
        else if (aqiValuePpm <= 15.4)
            subIndexValue_double = ((double) 49 / 2.9) * (aqiValuePpm - 12.5) + 151;
        else if (aqiValuePpm <= 30.4)
            subIndexValue_double = ((double) 99 / 14.9) * (aqiValuePpm - 15.5) + 201;
        else if (aqiValuePpm <= 50.4)
            subIndexValue_double = ((double) 199 / 19.9) * (aqiValuePpm - 30.5) + 301;
        else
            subIndexValue_double = 500;

        subIndexValue = Math.round(subIndexValue_double);

        return new Pair<Long, Double>(subIndexValue, Double.parseDouble(new DecimalFormat("#.##").format(aqiValuePpm)));
    }

    /* NO2 - dioxid de azot
     GOOD( 0 - 50 )               <=>  0 - 53 ppb
     MODERATE(51-100)             <=>  54 - 100 ppb
     Unhealthy_sensitive(101-150) <=>  101 - 360
     Unhealthy ( 151 - 200 )      <=>  361 - 649
     Very Unhealthy(201 - 300)    <=>  650 - 1249
     Hazardous( 301 - 500 )       <=>  1250 - 2049
    */
    public static Pair<Long, Double> GetSubIndexValue_NO2(double inputRawValue) {
        double subIndexValue_double = 0;
        long subIndexValue = 0;
        double sensitivity = -0.01648; // 16.48 nA/ppm,  0.01648 nA/ppb;

        double measuredVoltage = GetVoltageFromQuanta(inputRawValue); // => V

        //double voltage = 1.675 - measuredVoltage
        double nanoAmphereVal = ((1.675 - measuredVoltage) / 350000) * 1000000000;// (* 10 ^ 9) // ar trebui sa fie mai mic decat 1.675
        double aqiValuePpb = nanoAmphereVal / sensitivity;

        if (aqiValuePpb <= 0)
            subIndexValue_double = 0;
        else if (aqiValuePpb <= 53)
            subIndexValue_double = ((double) 50 / 53) * (aqiValuePpb - 0) + 0;
        else if (aqiValuePpb <= 100)
            subIndexValue_double = ((double) 49 / 46) * (aqiValuePpb - 54) + 51;
        else if (aqiValuePpb <= 360)
            subIndexValue_double = ((double) 49 / 259) * (aqiValuePpb - 101) + 101;
        else if (aqiValuePpb <= 649)
            subIndexValue_double = ((double) 49 / 288) * (aqiValuePpb - 361) + 151;
        else if (aqiValuePpb <= 1249)
            subIndexValue_double = ((double) 99 / 599) * (aqiValuePpb - 650) + 201;
        else if (aqiValuePpb <= 2049)
            subIndexValue_double = ((double) 199 / 799) * (aqiValuePpb - 1250) + 301;
        else
            subIndexValue_double = 500;

        subIndexValue = Math.round(subIndexValue_double);

        return new Pair<Long, Double>(subIndexValue, Double.parseDouble(new DecimalFormat("#.##").format(aqiValuePpb)));
    }

    /* SO2 - Sulfur Dioxide
     GOOD( 0 - 50 )               <=>  0 - 35 ppb
     MODERATE(51-100)             <=>  36 - 75 ppb
     Unhealthy_sensitive(101-150) <=>  76 - 185
     Unhealthy ( 151 - 200 )      <=>  186 - 304
     Very Unhealthy(201 - 300)    <=>  305 - 604
     Hazardous( 301 - 500 )       <=>  605 - 1004
    */
    public static Pair<Long, Double> GetSubIndexValue_SO2(double inputRawValue) {
        double subIndexValue_double = 0;
        long subIndexValue = 0;
        double sensitivity = 0.03722; // 16.48 nA/ppm,  0.01648 nA/ppb;

        double measuredVoltage = GetVoltageFromQuanta(inputRawValue); // => V

        double nanoAmphereVal = ((measuredVoltage - 0.5) / 350000) * 1000000000;// (* 10 ^ 9)
        double aqiValuePpb = nanoAmphereVal / sensitivity;

        if (aqiValuePpb <= 0)
            subIndexValue_double = 0;
        else if (aqiValuePpb <= 35)
            subIndexValue_double = ((double) 50 / 35) * (aqiValuePpb - 0) + 0;
        else if (aqiValuePpb <= 75)
            subIndexValue_double = ((double) 49 / 39) * (aqiValuePpb - 36) + 51;
        else if (aqiValuePpb <= 185)
            subIndexValue_double = ((double) 49 / 109) * (aqiValuePpb - 76) + 101;
        else if (aqiValuePpb <= 304)
            subIndexValue_double = ((double) 49 / 118) * (aqiValuePpb - 186) + 151;
        else if (aqiValuePpb <= 604)
            subIndexValue_double = ((double) 99 / 299) * (aqiValuePpb - 305) + 201;
        else if (aqiValuePpb <= 1004)
            subIndexValue_double = ((double) 199 / 399) * (aqiValuePpb - 605) + 301;
        else
            subIndexValue_double = 500;

        subIndexValue = Math.round(subIndexValue_double);

        return new Pair<Long, Double>(subIndexValue, Double.parseDouble(new DecimalFormat("#.##").format(aqiValuePpb)));
    }

    /* O3 - Sulfur Dioxide
     GOOD( 0 - 50 )               <=>  0 - 54 ppb
     MODERATE(51-100)             <=>  55 - 70 ppb
     Unhealthy_sensitive(101-150) <=>  71 - 85
     Unhealthy ( 151 - 200 )      <=>  86 - 105
     Very Unhealthy(201 - 300)    <=>  106 - 200
     Hazardous( 301 - 500 )       <=>  201 - 404
    */
    public static Pair<Long, Double> GetSubIndexValue_O3(double inputRawValue) {
        double subIndexValue_double = 0;
        long subIndexValue = 0;
        double sensitivity = -0.06773; // 16.48 nA/ppm,  0.01648 nA/ppb;

        double measuredVoltage = GetVoltageFromQuanta(inputRawValue); // => V

        double nanoAmphereVal = ((1.675 - measuredVoltage) / 350000) * 1000000000;// (* 10 ^ 9)
        double aqiValuePpb = nanoAmphereVal / sensitivity;

        if (aqiValuePpb <= 0)
            subIndexValue_double = 0;
        else if (aqiValuePpb <= 54)
            subIndexValue_double = ((double) 50 / 54) * (aqiValuePpb - 0) + 0;
        else if (aqiValuePpb <= 70)
            subIndexValue_double = ((double) 49 / 15) * (aqiValuePpb - 55) + 51;
        else if (aqiValuePpb <= 85)
            subIndexValue_double = ((double) 49 / 14) * (aqiValuePpb - 71) + 101;
        else if (aqiValuePpb <= 105)
            subIndexValue_double = ((double) 49 / 19) * (aqiValuePpb - 86) + 151;
        else if (aqiValuePpb <= 200)
            subIndexValue_double = ((double) 99 / 94) * (aqiValuePpb - 106) + 201;
        else if (aqiValuePpb <= 404)
            subIndexValue_double = ((double) 199 / 203) * (aqiValuePpb - 201) + 301;
        else
            subIndexValue_double = 500;

        subIndexValue = Math.round(subIndexValue_double);

        return new Pair<Long, Double>(subIndexValue, Double.parseDouble(new DecimalFormat("#.##").format(aqiValuePpb)));
    }

    public static int GetIntFromByteArray(byte[] inputByteArray) {
        ByteBuffer wrapped = ByteBuffer.wrap(inputByteArray); // big-endian by default
        return wrapped.getInt(); // 1
    }

    public static void SaveMeasurementToDatabase(DatabaseReference databaseReference, Measurements measurement) {
        String id = databaseReference.push().getKey();
        measurement.id = id;
        databaseReference.child(id).setValue(measurement);
    }
    public static void SaveMeasurementBMEToDatabase(DatabaseReference databaseReference, MeasurementsBME measurement) {
        String id = databaseReference.push().getKey();
        measurement.id = id;
        databaseReference.child(id).setValue(measurement);
    }

    private static double GetVoltageFromQuanta(double inputRawValue) {
        return (inputRawValue * 298) / 1000000000; // Output is the voltage in nanoVolts ( V )
    }

    public static int GetSecondsFromSpinner(String input) {
        switch (input) {
            case "Last 5min":
                return 300;
            case "Last 10min":
                return 600;
            case "Last 30min":
                return 1800;
            case "Last 1h":
                return 3600;
            case "Last 2h":
                return 7200;
            case "Last 4h":
                return 14400;
            case "Last 8h":
                return 28800;
            case "Last 12h":
                return 43200;
            case "Last 24h:":
                return 86400;
            case "Last 48h":
                return 172800;
            case "Last 72h":
                return 259200;
            default:
                return 3600;
        }
    }

    public static ArrayList<Measurements> GetTimeRelatedMeasurement(ArrayList<Measurements> measurementsDB, int secondsPeriod)
    {
        ArrayList<Measurements> measurementsInPeriod = new ArrayList<>();

        for (Measurements measure: measurementsDB)
        {
            long currentTime = System.currentTimeMillis() / 1000;
            if(measure.date >= currentTime - secondsPeriod )
                measurementsInPeriod.add(measure);
        }
        return measurementsInPeriod;
    }
    /*
    <item>Last 5min</item>
        <item>Last 10min</item>
        <item>Last 30min</item>
        <item>Last 1h</item>
        <item>Last 2h</item>
        <item>Last 8h</item>
        <item>Last 12h</item>
        <item>Last 24h</item>
        <item>Last 48h</item>
        <item>Last 72h</item>
     */

}
