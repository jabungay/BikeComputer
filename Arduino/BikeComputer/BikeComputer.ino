// Pre-written includes
#include <LiquidCrystal.h>
// Custom includes
#include "HelperFunctions.h"
#include "Pins.h"

const int HALL_THRESHOLD = 300;

int wheelDiameter, odometer;
File dataLogFile, parameterFile;

float wheelCircumference;
float bikeSpeed;

// Timer to store how long ago hall effect was triggered last
long timer;
// 
int timerStart;

bool triggered = false;



// Initialize the LCD
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
  wheelDiameter = (parameterString.substring(0, newLine)).toInt();
  odometer      = (parameterString.substring(newLine + 1)).toInt();
  
  lcd.print("Diameter: " + String(wheelDiameter));
  lcd.setCursor(0,1);
  lcd.print("Distance: " + String(odometer));

  delay(1000);
  
  timerStart = 0;
  timer = 0;
  bikeSpeed = 0;
  wheelCircumference = PI * wheelDiameter;
  Serial.println(wheelCircumference);

}

void loop()
{
  // Read the hall effect sensor
  int hallSensorValue = analogRead(HALL_SENSOR);

  // Timer to monitor how long it takes for one revolution
  timer = millis() - timerStart;

  if (millis() % 1000 == 0)
  {
    Serial1.println(hallSensorValue);
    lcd.clear();
    lcd.print(odometer / 1000);
    lcd.setCursor(0,1);
    lcd.print(bikeSpeed * 3.6);
  }

  //lcd.clear();
  //lcd.print(timer);
  
  if (hallSensorValue < HALL_THRESHOLD && !triggered)
  {
    // Flag to ensure that the timer doesn't get reset multiple times in one rotation of the wheel
    triggered = true;

    // Do the calculations for the speed and the wheelCircumference
    odometer += wheelCircumference;
    bikeSpeed = wheelCircumference / timer;

    Serial.print("Triggered! Odo: ");
    Serial.print(odometer);
    Serial.print(" Speed: ");
    Serial.println(bikeSpeed);

    // Increment the timerStart value so that the timer restarts from 0
    timerStart += timer;
  }
  else if (hallSensorValue > HALL_THRESHOLD && triggered)
  {
    // Reset the trigger if the magnet is far away and has recently been triggered
    triggered = false;  
  }
}
