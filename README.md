# About

The Secure Device Onboard All-In-One demo is designed to run different Secure Device Onboard
services like Manufacturer, IoT Platform SDK Service and Rendezvous Service within a single
instance of tomcat server. The purpose of this demo is to fast-track demonstration of Secure
Device Onboard protocol using different client implementations.

This is an example implementation to demonstrate out-of-box execution of these components.
Appropriate security measures with respect to key-store management and configuration management
should be considerd while performing production deployment using the components.

# Getting started with the SDO All-In-One Demo

The following are the system constraints for the All-in-One demo.
- Operating System: Ubuntu* 18.04
- Java* Development Kit 11
- Apache Maven* 3.5.4 (Optional) software for building the demo from source
- Java IDE (Optional) for convenience in modifying the source code
- Docker 18.09
- Docker compose 1.21.2
- Haveged

# Configuring JAVA execution environment

Appropriate proxy configuration should be updated in _JAVA_OPTIONS environment variable.

# Directory structure

The all-in-one-demo repository is structured into following 2 fodlers.
* `services` : The implementation for All-In-One demo services, which includes messages redirection
  as well automatic extension of vouchers and scheduling TO0 requests for them.

* `container` : The packaging of the demo is managed here. The overlay folder contains the
  configuration and data files that needs to be copied to final demo package.

# Building the SDO All-In-One Demo

The All-In-One demo is dependent on following repositories.

* `pri` : https://github.com/secure-device-onboard/pri

* `iot-platform-sdk` : https://github.com/secure-device-onboard/iot-platform-sdk

* `supply-chain-tools` : https://github.com/secure-device-onboard/supply-chain-tools

These repositories should be built using 'mvn clean install' command on the host machine before
building all-in-one-demo repository using following command.

```
mvn clean install
```

After successful build, the demo package is available at container/target/aio.tar.gz.

# Running the SDO All-In-One Demo

To run the demo, extract the demo package and follow the [README.md](container/overlay/README.md).
