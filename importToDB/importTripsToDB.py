"""
Reads NYC trip data and imports it into an SQL database

@author: Kyle Christianson
"""

import sys
import csv
import psycopg2
from trip import *

dbConnection = None
COMMIT_BATCH_SIZE = 100000

errorCodes = {
     0 : "VALID",
     1 : "BAD_GPS",
     2 : "ERR_GPS",
     3 : "BAD_LO_STRAIGHTLINE",
     4 : "BAD_HI_STRAIGHTLINE",
     5 : "ERR_LO_STRAIGHTLINE",
     6 : "ERR_HI_STRAIGHTLINE",
     7 : "BAD_LO_DIST",
     8 : "BAD_HI_DIST",
     9 : "ERR_LO_DIST",
     10 : "ERR_HI_DIST",
     11 : "BAD_LO_WIND",
     12 : "BAD_HI_WIND",
     13 : "ERR_LO_WIND",
     14 : "ERR_HI_WIND",
     15 : "BAD_LO_TIME",
     16 : "BAD_HI_TIME",
     17 : "ERR_LO_TIME",
     18 : "ERR_HI_TIME",
     19 : "BAD_LO_PACE",
     20 : "BAD_HI_PACE",
     21 : "ERR_LO_PACE",
     22 : "ERR_HI_PACE",
     23 : "ERR_DATE",
     24 : "ERR_OTHER",
     25 : "ERR_PARSING",
}

#########################################################################
# method to get a generic Python DB-API database connection
# this method is database dependent and will need to be modified
# depending on which database system we use
def connectToDb():
    global dbConnection
    dbConnection = psycopg2.connect(database='taxidata', user='taxiuser') #, host='localhost', password='test')

#########################################################################
# this method creates the taxi trip table
def createTripTable():
    sql = """
    CREATE TABLE IF NOT EXISTS trip ( 
    id INTEGER SERIAL PRIMARY KEY, 
    medallion VARCHAR(20), 
    hack_license VARCHAR(20), 
    vendor_id CHAR(3), 
    pickup_datetime TIMESTAMP,
    dropoff_datetime TIMESTAMP,
    passenger_count INTEGER,
    trip_time FLOAT,
    trip_distance FLOAT,
    trip_pace FLOAT,
    pickup_longitude FLOAT,
    pickup_latitude FLOAT,
    dropoff_longitude FLOAT,
    dropoff_latitude FLOAT);"""
    
    runSQL(sql)
    commitToDb()


#########################################################################
# this method drops the trip table
def dropTripTable():
    runSQL('DROP TABLE IF EXISTS trip')
    commitToDb()

#########################################################################
# runs an sql command and returns the cursor to retrieve the results
# this method does not commit changes, the caller is responsible
# to call commitToDb() when they are finished with the transaction
def runSQL(sql, args=None):
    global dbConnection
    if (dbConnection == None):
        raise Exception('Database not connected')
    cursor = dbConnection.cursor()
    if(args == None):
        cursor.execute(sql)
    else:
        cursor.execute(sql, args)
    return cursor

#########################################################################
# commits the transaction to persist it in the DB
def commitToDb():
    global dbConnection
    dbConnection.commit()



#########################################################################
# processes all of the NYC taxi trips in a csv file and inserts
# the valid trips to the database
def importFile(csvFile):

    tripCountBefore = runSQL('SELECT count(*) FROM trip').fetchone()[0]
    logMsg(' Database has ' + str(tripCountBefore) + ' trips before import')
    logMsg(' Parsing file ' + csvFile)
    tripsInFile = sum(1 for line in open(csvFile)) - 1
    logMsg(' Found ' + str(tripsInFile) + ' trips to process')

    errorCounts = {};
    taxiCounts = {};
    driverCounts = {};

    

    with open(csvFile, 'r') as filePointer:
        csvReader = csv.reader(filePointer)

        tripCount = 0

        # discard the csv header
        header = csvReader.next()
        del header
        
        for line in csvReader:
            tripCount += 1

            try:
                trip = Trip(line) # Parse the csv line into a trip object
            except ValueError:
                trip = None      # A trip of None indicates a parsing error
                addCount(errorCounts, 25) # error 25 for parsing error
                    
            if(trip != None):
                errCode = trip.isValid()
                addCount(errorCounts, errCode)

                if(errCode == 0):
                    # errCode 0 for a valid trip
                    # add counts and indert into DB
                    addCount(taxiCounts, trip.csvLine[0])
                    addCount(driverCounts, trip.driver_id)
                    
                    values = (trip.csvLine[0],
                              trip.csvLine[1],
                              trip.csvLine[2],
                              trip.pickup_time,
                              trip.dropoff_time,
                              int(trip.csvLine[7]),
                              trip.time,
                              trip.dist,
                              trip.pace,
                              trip.fromLon,
                              trip.fromLat,
                              trip.toLon,
                              trip.toLat)
                    
                    runSQL("INSERT INTO trip(medallion, hack_license, vendor_id, pickup_datetime, dropoff_datetime, passenger_count, trip_time, trip_distance, trip_pace, pickup_longitude, pickup_latitude, dropoff_longitude, dropoff_latitude) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)", values)
        
                    
            # commit to db every COMMIT_BATCH_SIZE valid trips
            if(errorCounts[0] % COMMIT_BATCH_SIZE == 0):
                logMsg(' Commiting ' + str(COMMIT_BATCH_SIZE) + ' trips to the DB')
                commitToDb()
                logMsg(' Commit successful. {:.2f}% complete'.format(tripCount / float(tripsInFile) * 100))

        # end for loop

    remainingTripsToCommit = errorCounts[0] % COMMIT_BATCH_SIZE
    if(remainingTripsToCommit > 0):
        logMsg(' Commiting remaining ' + str(remainingTripsToCommit) + ' trips to the DB')
        commitToDb()
        logMsg(' Commit successful. {:.2f}% complete'.format(tripCount / float(tripsInFile) * 100))      
        

    tripCountAfter = runSQL('SELECT count(*) FROM trip').fetchone()[0]
    logMsg(' Database has ' + str(tripCountAfter) + ' trips after import')


    print('\n\nSummary of file ' + csvFile + ':')
    print(' Total trips processed in csv file: ' + str(tripCount))
    print(' Total valid trips imported to DB: ' + str(tripCountAfter - tripCountBefore))
    print(' Total invalid trips skipped: ' + str(tripCount - errorCounts[0]))
    for key in errorCounts:
        print("    {:10d}  trips {}".format(errorCounts[key], errorCodes[key]))

    print("\n")

    #print(' Total unique taxis: ' + str(len(taxiCounts)))
    #print(' Total unique drivers: ' + str(len(driverCounts)))
    

#########################################################################
# increments the count at the dictionary key
def addCount(countDict, key):
    if key not in countDict:
        countDict[key] = 0
    countDict[key] += 1
    


#########################################################################
# MAIN
if(__name__=="__main__"):
    if(len(sys.argv) < 2):
        print('File argument is missing')
        exit()

    connectToDb()
    # uncomment this line to drop all data and start fresh
    #dropTripTable()
    createTripTable()
    for csvFile in sys.argv[1:]:
        importFile(csvFile)

# prints out the entire contents of the db, useful for testing
#    cursor = runSQL("SELECT * FROM trip") 
#    result = cursor.fetchall() 
#    for r in result:
#        print(r)

    dbConnection.close()

    
