package com.example.airqualityindex;

public class AqiUtils {

    //https://www3.epa.gov/airnow/aqi-technical-assistance-document-sept2018.pdf
    //inputRawValue - value of pollutant in ug/m3
    /*
     GOOD( 0 - 50 )               <=>    0    -    5038   ug/m3
     MODERATE(51-100)             <=>  5038   -    10.736
     Unhealthy_sensitive(101-150) <=>  10.736 -    14.198
     Unhealthy ( 151 - 200 )      <=>  14.198 -    17.633
     Very Unhealthy(201 - 300)    <=>  17.633 -    34.808
     Hazardous( 301 - 500 )       <=>  34.808 -    57.708
    */
    public static long GetSubIndexValue_CO(int inputRawValue)
    {
        double subIndexValue_double = 0;
        long subIndexValue = 0;

        if(inputRawValue < 5038)
            subIndexValue_double = ((50 - 0) / (5038 - 0)) * (inputRawValue - 0) + 0;
        else if(inputRawValue < 10.736)
            subIndexValue_double = ((100 - 51) / (10.736 - 5038)) * (inputRawValue - 5038) + 51;

        subIndexValue =  Math.round(subIndexValue_double);


        return subIndexValue ;
    }

}
