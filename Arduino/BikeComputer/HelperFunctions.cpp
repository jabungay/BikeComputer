#include "HelperFunctions.h"

String ReadFile(String file)
{
  File f = SD.open(&file[0]);
  String output = "";

  // If the file exists
  if (f)
  {
    while (f.available())
    {
      // Read a byte from the file
      byte c = f.read();

      // If the character is not a Line Feed (LF)
      // append it to the output string
      if (c != 13)
      {
        // Typecast the byte to a char
        output += (char) c;
      }
    }

    // Close the file when done
    f.close();

    return (output);
  }
  else
  {
    // If the file does not exist, return an error string
    return ("error, file does not exist");
  }
}

void AddDataPoint(float speed, long odometer)
{
  // Retrieve the csv file
  String dataFile = ReadFile("datalog.csv");

  // TODO: append speed and odometer reading as a new line on the CSV file and write it out
}

// Send a string of data over bluetooth
void SendData(String data)
{
  Serial1.println(data);
  Serial.print(data);
}
