#!/bin/bash

# This bash script will import multiple NYC csv files to the database multithreaded
# Call this script with all of the files as arguments

for arg in "$@"; do
    log="$arg.log"
    echo "Processing $arg in the background. Results in $log"
    python importTripsToDB.py "$arg" &> "$log" &
done
