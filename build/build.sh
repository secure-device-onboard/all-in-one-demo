#!/bin/bash

# Copyright 2020 Intel Corporation
# SPDX-License-Identifier: Apache 2.0

export http_proxy_host=$(echo $http_proxy | awk -F':' {'print $2'} | tr -d '/')
export http_proxy_port=$(echo $http_proxy | awk -F':' {'print $3'} | tr -d '/')

export https_proxy_host=$(echo $https_proxy | awk -F':' {'print $2'} | tr -d '/')
export https_proxy_port=$(echo $https_proxy | awk -F':' {'print $3'} | tr -d '/')

export _JAVA_OPTIONS="-Dhttp.proxyHost=$http_proxy_host -Dhttp.proxyPort=$http_proxy_port -Dhttps.proxyHost=$https_proxy_host -Dhttps.proxyPort=$https_proxy_port"

REPO_PRI=https://github.com/secure-device-onboard/pri.git
REPO_IOT=https://github.com/secure-device-onboard/iot-platform-sdk.git
REPO_SCT=https://github.com/secure-device-onboard/supply-chain-tools.git
REPO_AIO=https://github.com/secure-device-onboard/all-in-one-demo.git
REPO_RV=https://github.com/secure-device-onboard/rendezvous-service.git

# The same branch will be used to build other SDO components( PRI, IOT, RV & SCT).
REPO_BRANCH=master

cd /home/sdouser/
git clone $REPO_IOT
git clone $REPO_SCT
git clone $REPO_PRI
git clone $REPO_RV

cd /home/sdouser/iot-platform-sdk && git checkout $REPO_BRANCH
mvn clean install

cd /home/sdouser/supply-chain-tools && git checkout $REPO_BRANCH
mvn clean install

cd /home/sdouser/pri && git checkout $REPO_BRANCH
mvn clean install

cd /home/sdouser/rendezvous-service && git checkout $REPO_BRANCH
mvn clean install

if [ "$use_remote" = "1" ]; then
  echo "Building $REMOTE_URL : $REMOTE_BRANCH"
  cd /tmp/
  git clone $REPO_AIO
  cd /tmp/all-in-one-demo/
  git checkout $REPO_BRANCH
  mvn clean install
  cp -r ./container/target/ /home/sdouser/all-in-one-demo/container/
else
  echo "Building local copy"
  cd /home/sdouser/all-in-one-demo/
  mvn clean install
fi
