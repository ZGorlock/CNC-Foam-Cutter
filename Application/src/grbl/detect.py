#!/usr/bin/env python
"""\
    
    This script is called by stream.py to detect the appropriate port
    from which the arduino is connected to. If no arduino found it will
    raise an error, if multiple found it will use the first.

"""
import warnings
import serial.tools.list_ports

class Arduino(object):

    def __init__(self):
        arduino_ports = [
            p.device
            for p in serial.tools.list_ports.comports()
            if 'Arduino' in p.description
        ]

        if not arduino_ports:
            raise IOError("No Arduino found")
        if len(arduino_ports) > 1:
            warnings.warn('Multiple Arduinos found - using the first')  #Make it a pop up!!!

        self.port = serial.Serial(arduino_ports[0]).port

    def getPort(self):
        return self.port
