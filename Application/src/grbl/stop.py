import detect
import serial
self.s = serial.Serial(detect.Detect.getPort(),115200)
self.s.close()
