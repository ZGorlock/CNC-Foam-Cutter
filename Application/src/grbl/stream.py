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
import re
import time
import sys
import argparse
import warnings
import detect
# import threading
 

class Streamer(object):
    

    def __init__(self, quiet = None, settings = None):
        # get file to pass
        self.argumentGeneration(quiet, settings)
        
        #detect the arduino's port
        arduino = detect.Arduino()
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
        self.quiet = quiet
        self.settings = settings
        self.RX_BUFFER_SIZE = 128

    def startUp(self):
        # Initialize
        self.s = serial.Serial(self.port,115200)
        self.verbose = True
        if self.quiet : self.verbose = False
        self.settings_mode = False
        if self.settings : self.settings_mode = True

        # Wake up grbl
        # print ("Initializing grbl...")
        self.s.write("\r\n\r\n".encode())

        # Wait for grbl to initialize and flush startup text in serial input
        time.sleep(2)
        self.s.flushInput()

    def main(self):         
        # Stream g-code to grbl
        l_count = 0
        if self.settings_mode:
            # Send settings file via simple call-response streaming method. Settings must be streamed
            # in this manner since the EEPROM accessing cycles shut-off the serial interrupt.
            print ("SETTINGS MODE: Streaming", self.file, " to ", self.port)
            for line in self.file:
                l_count += 1 # Iterate line counter    
                # l_block = re.sub('\s|\(.*?\)','',line).upper() # Strip comments/spaces/new line and capitalize
                l_block = line.strip() # Strip all EOL characters for consistency
                self.s.write((l_block + '\n').encode()) # Send g-code block to grbl
                grbl_out = self.s.readline().strip() # Wait for grbl response with carriage return
        else:    
            # Send g-code program via a more agressive streaming protocol that forces characters into
            # Grbl's serial read buffer to ensure Grbl has immediate access to the next g-code command
            # rather than wait for the call-response serial protocol to finish. This is done by careful
            # counting of the number of characters sent by the streamer to Grbl and tracking Grbl's 
            # responses, such that we never overflow Grbl's serial read buffer. 
            g_count = 0
            c_line = []
            # periodic() # Start status report periodic timer
            for line in self.file:
                l_count += 1 # Iterate line counter
                l_block = re.sub('\s|\(.*?\)','',line).upper() # Strip comments/spaces/new line and capitalize
                l_block = line.strip()
                c_line.append(len(l_block)+1) # Track number of characters in grbl serial read buffer
                grbl_out = ''
                while sum(c_line) >= self.RX_BUFFER_SIZE-1 | self.s.inWaiting() :
                    out_temp = self.s.readline().strip() # Wait for grbl response
                    if out_temp.find('ok'.encode()) < 0 and out_temp.find('error'.encode()) < 0 :
                        print ("  Debug: ",out_temp) # Debug response
                    else :
                        grbl_out += out_temp.decode()
                        g_count += 1 # Iterate g-code counter
                        grbl_out += str(g_count); # Add line finished indicator
                        del c_line[0] # Delete the block character count corresponding to the last 'ok'
                self.s.write((l_block + '\n').encode()) # Send g-code block to grbl
                grbl_out = self.s.readline().decode()

        # Close file and serial port
        self.s.write("?/n".encode())
        print(self.s.readline().decode())
        self.file.close()
        self.s.close()

streamer = Streamer()

