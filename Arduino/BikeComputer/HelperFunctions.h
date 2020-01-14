#ifndef DATALOG_H
#define DATALOG_H

#include <SPI.h>
#include <SD.h>
#include <String.h>

// Read a file and return the contents
// as a string
String readFile(const String file);

#endif
