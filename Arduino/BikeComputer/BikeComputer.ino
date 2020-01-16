// Pre-written includes
#include <LiquidCrystal.h>
// Custom includes
#include "HelperFunctions.h"
#include "DataLog.h"
#include "Pins.h"

// Bike parameters that will be retrieved from parameters file
int wheelDiameter, odometer;

// Bike values that will be calculated
float wheelCircumference, bikeSpeed;

// Timer to store how long ago hall effect was triggered last
long timer, timerStart;

// Store whether or not the Hall Sensor has been triggered
bool triggered;

LiquidCrystal lcd(RS, EN, D4, D5, D6, D7);

void setup()
{
  // Initialize 16x2 character display
  lcd.begin(16,2);

  // Initialize serial monitor
  Serial.begin(115200);

  // Initialize bluetooth serial
  Serial1.begin(115200);

  // Retrieve wheelDiameter and odometer from parameters.txt
  String parameterString = readFile("parameters.txt");
  int newLine   = parameterString.indexOf((char) 10);
  // Assign retrieved values
  wheelDiameter = (parameterString.substring(0, newLine)).toInt();
  odometer      = (parameterString.substring(newLine + 1)).toInt();

  // Initial ariable assignments
  timerStart = 0;
  timer = 0;
  bikeSpeed = 0;
  triggered = false;

  lcd.print("Diameter: " + String(wheelDiameter));
  lcd.setCursor(0,1);
  lcd.print("Distance: " + String(odometer));

  delay(1000);

  // Calculate wheelCircumference C = PI * d
  wheelCircumference = PI * wheelDiameter;

}

void loop()
{
  // Read the hall effect sensor
  int hallSensorValue = analogRead(HALL_SENSOR);

  // Timer to monitor how long it takes for one revolution
  timer = millis() - timerStart;

  // Update the display every second
  if (millis() % 1000 == 0)
  {
    Serial1.println(hallSensorValue);
    lcd.clear();
    lcd.print(odometer / 1000);
    lcd.setCursor(0,1);
    lcd.print(bikeSpeed * 3.6);
  }

  // Execute this if the magnet comes close enough to the sensor
  if (hallSensorValue < HALL_THRESHOLD && !triggered)
  {
    // Flag to ensure that the timer doesn't get reset multiple times in one rotation of the wheel
    triggered = true;

    // Do the calculations for the speed and the wheelCircumference
    odometer += wheelCircumference;
    bikeSpeed = wheelCircumference / timer;

    // Increment the timerStart value so that the timer restarts from 0
    timerStart += timer;
  }
  // Execute this when the magnet leaves the sensor's proximity
  else if (hallSensorValue > HALL_THRESHOLD && triggered)
  {
    // Reset the trigger if the magnet is far away and has recently been triggered
    triggered = false;
  }
}
