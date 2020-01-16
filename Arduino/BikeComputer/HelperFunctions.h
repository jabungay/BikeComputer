#ifndef DATALOG_H
#define DATALOG_H

#include <SPI.h>
#include <SD.h>
#include <String.h>

// Read a file and return the contents
// as a string
String ReadFile(const String file);

// Function to add a data point to the pre-existing csv file
void AddDataPoint(float speed, long odometer);

#endif
