#!/bin/sh
sudo -u postgres /opt/local/lib/postgresql95/bin/pg_ctl -D /opt/local/var/db/postgresql95/taxidata -l /opt/local/var/db/postgresql95/taxidata/server.log start
