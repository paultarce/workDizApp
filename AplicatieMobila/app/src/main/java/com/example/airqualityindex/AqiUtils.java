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
    public static long GetSubIndexValue_CO(double inputRawValue)
    {
        double subIndexValue_double = 0;
        long subIndexValue = 0;

        if(inputRawValue < 5038)
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
            subIndexValue_double = ((500 - 301) / (57708 - 34809)) * (inputRawValue - 34809) + 301;

        subIndexValue = Math.round(subIndexValue_double);

        return subIndexValue ;

    }

}
