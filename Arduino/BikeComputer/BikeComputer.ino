// Pre-written includes
#include <LiquidCrystal.h>

// Custom includes
#include "HelperFunctions.h"

float wheelDiameter, odometer;
File dataLogFile, parameterFile;

// Initialize the LCD
//const int rs = 12, en = 11, d4 = 5, d5 = 4, d6 = 3, d7 = 2;
//LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

void setup() 
{
  // Initialize 16x2 character display
//  lcd.begin(16,2);
  // Initialize serial monitor
  Serial.begin(9600);

  // Retrieve wheelDiameter and odometer from parameters.txt
  String parameterString = readFile("parameters.txt");
  int newLine   = parameterString.indexOf((char) 10);
  wheelDiameter = (parameterString.substring(0, newLine))).toInt();
  odometer      = (parameterString.substring(newLine + 1))).toInt(); 

}

void loop()
{
  // put your main code here, to run repeatedly:

}
