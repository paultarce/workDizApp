package com.example.airqualityindex;
import android.widget.Toast;

import com.example.airqualityindex.Models.Measurements;
import com.google.firebase.database.DatabaseReference;

import java.nio.ByteBuffer;
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
    public static long GetSubIndexValue_CO(double inputRawValue)// I Get ppb from sensor
    {
        double subIndexValue_double = 0;
        long subIndexValue = 0;

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
        inputRawValue = inputRawValue / 1000; // Transform to ppm - as the standard is for CO
        if (inputRawValue <= 4.4)
            subIndexValue_double = (50 / 4.4) * (inputRawValue - 0) + 0;
        else if (inputRawValue <= 9.4)
            subIndexValue_double = (49 / 4.9) * (inputRawValue - 4.5) + 51;
        else if (inputRawValue <= 12.4)
            subIndexValue_double = (49 / 2.9) * (inputRawValue - 9.5) + 101;
        else if (inputRawValue <= 15.4)
            subIndexValue_double = (49 / 2.9) * (inputRawValue - 12.5) + 151;
        else if (inputRawValue <= 30.4)
            subIndexValue_double = (99 / 14.9) * (inputRawValue - 15.5) + 201;
        else if (inputRawValue <= 50.4)
            subIndexValue_double = (199 / 19.9) * (inputRawValue - 30.5) + 301;

        subIndexValue = Math.round(subIndexValue_double);

        return subIndexValue;

    }

    public static int GetIntFromByteArray(byte[] inputByteArray) {
        ByteBuffer wrapped = ByteBuffer.wrap(inputByteArray); // big-endian by default
        return wrapped.getInt(); // 1
    }


    public static void SaveMeasurementToDatabase(DatabaseReference databaseAQI, Measurements measurement)
    {

       String id = databaseAQI.push().getKey();
       measurement.id = id;

       databaseAQI.child(id).setValue(measurement);


    }

}
