#!/usr/bin/env bash

# ##########################################################################################################
# This script starts the webpack dev server using the locally installed node, yarn and webpack.
# This is particularly useful in environments that do not have a global node, yarn and webpack installation.
# ##########################################################################################################

# add node and yarn to path
PATH=`pwd`/node:`pwd`/node/yarn/dist/bin:$PATH

# parse appServerUrl argument
if [ ! -z "$1" ]
  then
  	export appServerUrl=$1
fi

if [ -z "$appServerUrl" ]
  then
  	export appServerUrl="http://localhost:8080"
    echo "appServerUrl is not set! Setting to default: " $appServerUrl
  else
  	echo "appServerUrl set to " $appServerUrl
fi

export PORT=${2:-9000}
echo "Dev server will be available on port " $PORT " or higher if already in use...";

# start dev server
yarn dev