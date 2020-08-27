# About

The Secure Device Onboard All-In-One demo is designed to run different Secure Device Onboard
services like Manufacturer, IoT Platform SDK Service and Rendezvous Service from a single
**docker** instance. The purpose of this demo is to fast-track demonstration of Secure
Device Onboard protocol using different client implementations.

**NOTE**: The All-In-One demo is provided solely to demonstrate out-of-box operation of Secure Device Onboard components. _This demo is not recommended for use in any production capacity._  Appropriate security measures with respect to key-store management and configuration management should be considered while performing production deployment of any Secure Device Onboard component.

# Getting Started with the SDO All-In-One Demo

The following are the system constraints for the All-in-One demo.
- Operating System: Ubuntu* 18.04
- Java* Development Kit 11
- Apache Maven* 3.5.4 (Optional) software for building the demo from source
- Java IDE (Optional) for convenience in modifying the source code
- Docker 18.09
- Docker compose 1.21.2
- Haveged

# Configuring JAVA execution environment

Appropriate proxy configuration should be updated in **`_JAVA_OPTIONS`** environment variable. (Mandatory, if you are working behind a proxy.)

Update the proxy information in _JAVA_OPTIONS as ```_JAVA_OPTIONS=-Dhttp.proxyHost=http_proxy_host -Dhttp.proxyPort=http_proxy_port -Dhttps.proxyHost=https_proxy_host -Dhttps.proxyPort=https_proxy_port```.

# Directory structure

The all-in-one-demo repository is structured into following 2 folders.
* `services` : The implementation for All-In-One demo services, which includes messages redirection
  as well automatic extension of vouchers and scheduling TO0 requests for them.

* `container` : The packaging of the demo is managed here. The overlay folder contains the
  configuration and data files that needs to be copied to final demo package.

# Building the SDO All-In-One Demo

The All-In-One demo is dependent on following repositories.

* `pri` : https://github.com/secure-device-onboard/pri

* `iot-platform-sdk` : https://github.com/secure-device-onboard/iot-platform-sdk

* `supply-chain-tools` : https://github.com/secure-device-onboard/supply-chain-tools

* `rendezvous-service` : https://github.com/secure-device-onboard/rendezvous-service

These repositories should be built using 'mvn clean install' command on the host machine before
building all-in-one-demo repository using following command.

```
mvn clean install
```

Alternatively, you have to option to build All-in-One demo using Docker.[Read more](build/README.md)

```
cd build/
docker-compose up --build
```

After successful build, the demo package is available at demo/aio.tar.gz.

# Running the SDO All-In-One Demo

To run the demo, extract the demo package and follow the [README.md](container/overlay/README.md).
