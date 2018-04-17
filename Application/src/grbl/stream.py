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
import sys
# import threading


class Streamer(object):


    def __init__(self, quiet = None, settings = None, started = 1):

        # get file to pass
        self.argumentGeneration(quiet, settings)

        #detect the arduino's port
        arduino = detect.Detect()
        self.port = arduino.getPort()
        self.started = 0
        # start grbl settings
        self.startUp()

        # stream code
        self.main()

    def argumentGeneration(self, quiet = None, settings = None):
        # Define command line argument interface
        parser = argparse.ArgumentParser(description='Stream g-code file to grbl. (pySerial and argparse libraries required)')
        parser.add_argument('gcode_file', type=argparse.FileType('r'),
        help='g-code filename to be streamed')
        parser.add_argument('started', type=int)
        args = parser.parse_args()
        self.file = args.gcode_file
        self.started = args.started

    def str2bool(v):
        if v.lower() in ('yes', 'true', 't', 'y', '1'):
            return True
        elif v.lower() in ('no', 'false', 'f', 'n', '0'):
            return False
        else:
            raise argparse.ArgumentTypeError('Boolean value expected.')

    def startUp(self):
        # Initialize
        self.s = serial.Serial(self.port,115200)

        if self.started != 1:
            # Wake up grbl
            # print ("Initializing grbl...")
            self.s.write("\r\n\r\n".encode())

            # Wait for grbl to initialize and flush startup text in serial input
            time.sleep(2)
            self.s.flushInput()

    def main(self):

        for line in self.file:
            l = line.strip()
            #print(l)
            self.s.write((l + '\n').encode())
            time.sleep(1)
            response = ''

            while(response.split(",")[0][1:] != 'Idle'):
                self.s.write(('?\n').encode())
                time.sleep(1)
                response = self.s.readline().decode()

                #print(justwaiting, " is this")
                # while(len(justwaiting) < 6):
                #     print(justwaiting, " is that")
                #     response = self.s.readline().decode()
                #justwaiting = response.split(",")[0][1:]
                #print("split", justwaiting)
                #justwaiting = justwaiting[0][1:].copy()
                #print("what we want", justwaiting)

            print(response)

        # Close file and serial port
        self.file.close()
        self.s.close()

streamer = Streamer()


