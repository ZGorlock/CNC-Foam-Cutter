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
G1 X200.4101 Y100.4101 U200.4101 V100.4101
G1 X0 Y300.3335 U0 V300.3335
G1 X-20.0821 Y250.6564 U-20.0821 V250.6564
G1 X-200.3179 Y0 U-200.3179 V
G1 X0 Y-10 U0 V-10
G1 X-10 Y0 U-10 V0
M5
M2
