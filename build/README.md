# About

Docker Script for Building All-in-one-demo. Using this script you can build the local copy of the repository as well as the latest upstream of the repository.

## Prerequisites

- Operating system: **Ubuntu 18.04.**

- Docker engine : **18.06.0**

- Docker-compose : **1.23.2**


## Usage

When you want to build a local copy of the repository.

``` sudo docker-compose up --build ```

When you want to build the latest upstream of the repository.

``` sudo use_remote=1 docker-compose up --build ```

You also have the option to change the remote repository URL as well as the remote repository branch in build.sh file.

    REPO_NAME=link-to-your-fork
    REPO_BRANCH=branch-name

## Expected Outcome
After successful build, the demo package is available at demo/aio.tar.gz.

## Updating Proxy Info (Optional )
If you are working behind a proxy network, ensure that both HTTP and HTTPS proxy variables are set.

    export http_proxy=http-proxy-host:http-proxy-port
    export https_proxy=https-proxy-host:https-proxy-port
