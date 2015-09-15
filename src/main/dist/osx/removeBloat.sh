#!/bin/bash

cd Murcs.app/Contents/Java
find ! -name 'sws' -type d -exec rm -r -f {} +
cd ../../../