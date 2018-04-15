#!/usr/bin/env python
"""\
Stream g-code to grbl controller
This script differs from the simple_stream.py script by 
tracking the number of characters in grbl's serial read
buffer. This allows grbl to fetch the next line directly
from the serial buffer and does not have to wait for a 
response from the computer. This effectively adds another
buffer layer to prevent buffer starvation.
CHANGELOG:
- 20140714: Updated baud rate to 115200. Added a settings
  write mode via simple streaming method. MIT-licensed.
TODO: 
- Add runtime command capabilities
---------------------
The MIT License (MIT)
Copyright (c) 2012-2014 Sungeun K. Jeon
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
---------------------
"""

import serial
import time
import argparse
import detect
# import threading
 

class Streamer(object):
    

    def __init__(self, quiet = None, settings = None):
        # get file to pass
        self.argumentGeneration(quiet, settings)
        
        #detect the arduino's port
        arduino = detect.Detect()
        self.port = arduino.getPort()

        # start grbl settings
        self.startUp()

        # stream code
        self.main()

    def argumentGeneration(self, quiet = None, settings = None):
        # Define command line argument interface
        parser = argparse.ArgumentParser(description='Stream g-code file to grbl. (pySerial and argparse libraries required)')
        parser.add_argument('gcode_file', type=argparse.FileType('r'),
        help='g-code filename to be streamed')
        args = parser.parse_args()
        self.file = args.gcode_file

    def startUp(self):
        # Initialize
        self.s = serial.Serial(self.port,115200)
        
        # Wake up grbl
        # print ("Initializing grbl...")
        self.s.write("\r\n\r\n".encode())

        # Wait for grbl to initialize and flush startup text in serial input
        time.sleep(2)
        self.s.flushInput()

    def main(self):         
        
        for line in self.file:
            l = line.strip()
            self.s.write((l + '\n').encode())
            time.sleep(1)
            self.s.write(('?\n').encode())
            time.sleep(1)
            response = ''

            while(len(response) < 6):
                response = self.s.readline().decode()
            print(response)

        # Close file and serial port
        self.file.close()
        self.s.close()

streamer = Streamer()

