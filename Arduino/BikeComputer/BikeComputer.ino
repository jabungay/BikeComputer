// Pre-written includes
#include <LiquidCrystal.h>
// Custom includes
#include "HelperFunctions.h"
#include "Pins.h"

int wheelDiameter, odometer;
File dataLogFile, parameterFile;

float wheelCircumference;
float bikeSpeed;

// Timer to store how long ago hall effect was triggered last
long timer;
// 
int timerStart;



// Initialize the LCD
LiquidCrystal lcd(RS, EN, D4, D5, D6, D7);

void setup() 
{
  // Initialize 16x2 character display
  lcd.begin(16,2);
  // Initialize serial monitor
  Serial.begin(9600);

  // Retrieve wheelDiameter and odometer from parameters.txt
  String parameterString = readFile("parameters.txt");
  int newLine   = parameterString.indexOf((char) 10);
  wheelDiameter = (parameterString.substring(0, newLine)).toInt();
  odometer      = (parameterString.substring(newLine + 1)).toInt();

  lcd.print("Diameter: " + String(wheelDiameter));
  lcd.setCursor(0,1);
  lcd.print("Distance: " + String(odometer));
  
  timerStart = 0;
  timer = 0;
  bikeSpeed = 0;
  wheelCircumference = PI * wheelDiameter;

}

void loop()
{
  // Read the hall effect sensor
  int hallSensorValue = analogRead(HALL_SENSOR);

  // Timer to monitor how long it takes for one revolution
  timer = millis() / 1000 - timerStart;
  
  if (/*sensor reaches maximum*/0)
  {
    odometer += wheelCircumference;
    bikeSpeed = wheelCircumference / timer;

    timerStart = timer;
    
  }
}
