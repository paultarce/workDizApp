package com.example.airqualityindex;

public class AqiUtils {

    //https://www3.epa.gov/airnow/aqi-technical-assistance-document-sept2018.pdf
    //inputRawValue - value of pollutant in ug/m3
    /* CO - carbon monoxide
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
            subIndexValue_double = ((100 - 51) / (10.736 - 5039)) * (inputRawValue - 5039) + 51;
        else if(inputRawValue < 14.198)
            subIndexValue_double = ((150 - 101) / (14.198 - 10.737)) * (inputRawValue - 10.737) + 101;
        else if(inputRawValue < 17.633)
            subIndexValue_double = ((200 - 151) / (17.633 - 14.199)) * (inputRawValue - 14.199) + 151;
        else if(inputRawValue < 34.808)
            subIndexValue_double = ((300 - 201) / (34.808 - 17.634)) * (inputRawValue - 17.634) + 201;
        else if(inputRawValue < 57.708)
            subIndexValue_double = ((500 - 301) / (57.708 - 34.809)) * (inputRawValue - 34.809) + 301;

        subIndexValue = Math.round(subIndexValue_double);

        return subIndexValue ;

    }

}
