#!/usr/bin/env perl -w
use strict;
use Date::Parse;

sub addToDB {
    my @args = ('redis-cli', '-p', '6385');
    push(@args, @_);
    if (system('/bin/bash -c "' . join(' ', @args) . '" > /dev/null 2>&1')) {
        die("Failed to run command: " . @args);
    }
}

my $dataFile = $ARGV[0];
open DATAFILE, $dataFile or die("Could not open $dataFile: " . $!);
print "Reading $dataFile\n";
my $line = <DATAFILE>;
my $count = 0;
while ($line = <DATAFILE>) {
    $count++;
    if ($count % 1000 == 0) {
        print "$count\n";
    }
    my ($medallion,
        $hack,
        $vendor,
        $rateCode,
        $unknown,
        $pickupTime,
        $dropoffTime,
        $passengers,
        $duration,
        $distance,
        $pickupLon,
        $pickupLat,
        $dropoffLon,
        $dropoffLat) = split(',', $line);

    $pickupTime = str2time($pickupTime, "EST");
    $dropoffTime = str2time($dropoffTime, "EST");

    my $hashKey = "$medallion-$hack-$pickupTime";
    addToDB("zadd", "requests", $pickupTime, $hashKey);
    addToDB("hset", $hashKey, "pickupTime", $pickupTime);
    addToDB("hset", $hashKey, "dropoffTime", $dropoffTime);
    addToDB("hset", $hashKey, "passengers", $passengers);
    addToDB("hset", $hashKey, "pickupLon", $pickupLon);
    addToDB("hset", $hashKey, "pickupLat", $pickupLat);
    addToDB("hset", $hashKey, "dropoffLon", $dropoffLon);
    addToDB("hset", $hashKey, "dropoffLat", $dropoffLat);
}
close DATAFILE;
