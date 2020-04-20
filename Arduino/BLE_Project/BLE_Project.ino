
#include <SPI.h>
#include <Arduino.h>
#include <SoftwareSerial.h>
#include "MQ7.h"

int Bluetooth1 = 2;   //RX sau TX
int Bluetooth2 = 4;
SoftwareSerial bluetooth(Bluetooth1,Bluetooth2);   //RX, TX
MQ7 mq7(A0,5.0);

//CO Sensor 
const int GasSensorPin = A0;
const int DOUTpin = 8;
float GasSensorVlaue_ppm = 0;
int GasSensorVlaue_ppb = 0;
int limit;


void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  bluetooth.begin(9600);

  pinMode(Bluetooth1, INPUT);
  pinMode(Bluetooth2, OUTPUT);

  pinMode(DOUTpin, INPUT);
}


void loop() {
   
   // put your main code here, to run repeatedly:


   GasSensorVlaue_ppm = mq7.getPPM();
   Serial.print("PPM: ");
   Serial.println(GasSensorVlaue_ppm);

   GasSensorVlaue_ppb = GasSensorVlaue_ppm * 1000;
   bluetooth.print(GasSensorVlaue_ppb);

  if(bluetooth.available())
  {  
    String a = bluetooth.readString();
    Serial.println("Received:"); Serial.println(a);         
  }

  delay(2000);
     
}


/*
float sensor_volt;
float RS_gas; 
float R0;
int R2 = 2000;
  
void setup() {
 Serial.begin(9600);
}
 
void loop() {
  int sensorValue = analogRead(A0);
  sensor_volt=(float)sensorValue/1024*5.0;
  RS_gas = ((5.0 * R2)/sensor_volt) - R2;
  R0 = RS_gas / 1;
  Serial.print("R0: ");
  Serial.println(R0);
}

float RS_gas = 0;
float ratio = 0;
float sensorValue = 0;
float sensor_volt = 0;
float R0 = 7200.0;
 
void setup() {
 Serial.begin(9600);
}
 
void loop() {
   sensorValue = analogRead(A0);
   sensor_volt = sensorValue/1024*5.0;
   RS_gas = (5.0-sensor_volt)/sensor_volt;
   ratio = RS_gas/R0; //Replace R0 with the value found using the sketch above
   float x = 1538.46 * ratio;
   float ppm = pow(x,-1.709);
   Serial.print("PPM: ");
   Serial.println(ppm);
   delay(1000);
}
*/
