#!/bin/sh
#
# This version has been modified to work with 720P H.264 encoding.
#

rmmod cmemk 2>/dev/null

modprobe cmemk allowOverlap=1 phys_start=0x83700000 phys_end=0x85900000 \
     pools=2x5531400,3x2457600,2x921600,1x345600,1x691200,1x1

# insert DSP/BIOS Link driver
modprobe dsplinkk

# insert Local Power Manager driver
modprobe lpm_omap3530

# insert SDMA driver
modprobe sdmak 

