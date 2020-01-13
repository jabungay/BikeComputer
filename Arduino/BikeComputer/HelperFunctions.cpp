#include "HelperFunctions.h"

void writeInt(byte addr, int data) {
  byte upper = highByte(data);
  byte lower = lowByte(data);

  EEPROM.write(addr, upper);
  EEPROM.write(addr + 1, lower);

  EEPROM.commit();
}
