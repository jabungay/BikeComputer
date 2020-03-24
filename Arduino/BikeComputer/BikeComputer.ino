// Pre-written includes
#include <LiquidCrystal.h>
// Custom includes
#include "HelperFunctions.h"
#include "Pins.h"

// Bike parameters that will be retrieved from parameters file
int wheelDiameter;
unsigned long odometer, lastOdo;

// Bike values that will be calculated
float wheelCircumference, bikeSpeed, lastSpeed;

// Timer to store how long ago hall effect was triggered last
unsigned long timer, timerStart;

// Store whether or not the Hall Sensor has been triggered
bool triggered;

LiquidCrystal lcd(RS, EN, D4, D5, D6, D7);

void sensorInterrupt()
{
    odometer += wheelCircumference;
    bikeSpeed = wheelCircumference / (millis() - timerStart);
    timerStart = millis();
    String data = "{'odometer' :" + String(odometer) + ", 'speed' :" + (String)((int)(bikeSpeed * 1000)) + "} \n";
    SendData(data); 
}

void setup()
{
  // Initialize 16x2 character display
  lcd.begin(16,2);

  // Initialize serial monitor
  Serial.begin(115200);

  // Initialize bluetooth serial
  Serial1.begin(115200);

  // Retrieve wheelDiameter and odometer from parameters.txt
  String parameterString = ReadFile("parameters.conf");
  int newLine   = parameterString.indexOf((char) 10);
  // Assign retrieved values
  wheelDiameter = (parameterString.substring(0, newLine)).toInt();
  odometer      = (parameterString.substring(newLine + 1)).toInt();

  // Initial variable assignments
  timerStart = 0;
  timer = 0;
  bikeSpeed = 0;
  lastSpeed = 0;
  lastOdo = odometer;
  triggered = false;

  lcd.print("Diameter: " + String(wheelDiameter));
  lcd.setCursor(0,1);
  lcd.print("Distance: " + String(odometer));

  delay(1000);

  // Calculate wheelCircumference C = PI * d
  wheelCircumference = PI * wheelDiameter;

  attachInterrupt(HALL_SENSOR, sensorInterrupt, RISING);

  lcd.clear();
  lcd.setCursor(11,0);
  lcd.print("km");
  lcd.setCursor(11,1);
  lcd.print("km/h");
}

void loop()
{
  // Timer to monitor how long it takes for one revolution
  timer = millis() - timerStart;

  if (lastOdo != odometer)
  {
    lcd.setCursor(0,0);
    lcd.print(odometer / 1000000.0, 3);
    lastOdo = odometer;
  }
  if (lastSpeed != bikeSpeed)
  {
    lcd.setCursor(0,1);
    lcd.print(bikeSpeed * 3.6, 1);
    lastSpeed = bikeSpeed;
  }

  // Read data from the bluetooth serial port
  while (Serial1.available() > 0)
  {
    String str = Serial.readString();
    Serial.println(str);
  }
}
