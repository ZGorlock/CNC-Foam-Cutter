+
G17
G21
G91
( SET CUTTER COMPENSATION )
G40
( SET TOOL LENGTH OFFSET )
G49
( SET PATH CONTROL MODE )
G64
( SET FEED RATE MODE )
G94

M3
S100
F10
G1 X0 Y20 U0 V20
G4 P2000
G1 X10 Y0 U10 V0
G1 X30.4101 Y50.4101 U40.4101 V40.4101
G1 X80.3335 Y0 U80.3335 V0
G1 X20.6564 Y-10.0821 U20.6564 V-10.0821
G1 X10 Y-30.3179 U0 V-21.3179
G1 X10 Y30.3179 U0 V-21.3179
G1 X0 Y-60.3179 U0 V-21.3179
M5
M2
