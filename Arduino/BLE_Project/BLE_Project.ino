
#include <SPI.h>
#include <Arduino.h>
#include <SoftwareSerial.h>


int Bluetooth1 = 2;   //RX sau TX
int Bluetooth2 = 4;
SoftwareSerial bluetooth(Bluetooth1,Bluetooth2);   //RX, TX

//CO Sensor 
const int GasSensorPin = A0;
const int DOUTpin = 8;
int GasSensorVlaue = 0;
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

  GasSensorVlaue = analogRead(GasSensorPin);
  limit = digitalRead(DOUTpin);
  /*Serial.println("Limit = ");
  Serial.print(limit);*/
  Serial.println("CO Value = ");
  Serial.println(GasSensorVlaue);
  delay(2000);
  bluetooth.write(GasSensorVlaue);
  if(bluetooth.available())
  {
    
    String a = bluetooth.readString();
    Serial.println("Received:"); Serial.println(a);
    
   //bluetooth.write(GasSensorValue);
   //bluetooth 
    //delay(100);  
  }

  
  
  //Serial.print("2");
  //Serial.print("\n");
  //Serial.print("AT+BIND?");
 // delay(300);
}
