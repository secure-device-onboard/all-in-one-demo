#!/bin/bash

# Starting redis-server as a UNIX daemon process and
# putting the process to sleep state for 1 second
redis-server --daemonize yes && sleep 1

# Adding default hashlist to the redis-server
redis-cli < /usr/local/hashlist.redis
redis-cli save
redis-cli shutdown

# Running the redis server
redis-server
