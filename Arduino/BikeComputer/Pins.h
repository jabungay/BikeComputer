#ifndef PINS_H
#define PINS_H

/*
 * The Intel Galileo's built-in SD card reader is connected as follows:
 * MOSI - pin 11
 * MISO - pin 12
 * CLK  - pin 13 
 * CS   - pin  4
 */

// For hall effect sensor
#define HALL_SENSOR A0

// For serial bluetooth
#define RX 0
#define TX 1

// For LCD (4-bit mode)
#define RS 10
#define EN 9
#define D4 8
#define D5 7
#define D6 6
#define D7 5


#endif
