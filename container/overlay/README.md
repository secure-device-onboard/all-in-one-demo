# About

The Secure Device Onboard (SDO) All-In-One demo is designed to run SDO Manufacturer service, SDO IoT
Platform SDK Service and SDO Rendezvous Service from a single **docker** instance. The
purpose of this demo is to fast-track demonstration of Secure Device Onboard protocol using
different client implementations.

# Getting Started with the SDO All-In-One Demo

The following are the system constraints for the All-in-One demo.
- Operating System: Ubuntu* 20.04
- Java* Runtime Environment 11
- Docker 18.09
- Docker compose 1.21.2
- Haveged
- Postman (Optional) to execute REST calls

# Configuring JAVA Execution Environment

Appropriate proxy configuration should be updated in _JAVA_OPTIONS environment variable.

Update the proxy information in _JAVA_OPTIONS as ```_JAVA_OPTIONS=-Dhttp.proxyHost=http_proxy_host -Dhttp.proxyPort=http_proxy_port -Dhttps.proxyHost=https_proxy_host -Dhttps.proxyPort=https_proxy_port```.

# Running All-In-One Demo

The All-In-One demo can be executed only as a docker service. At the
end of initialization of all services, you will see following statement on the console.

`Completed Initialization in <Time> ms.`

Follow the options below to start All-In-One demo.

##  Run as Docker Service

Open a terminal, change directory to the root of extracted aio.tar.gz and execute following command.

```
docker-compose up --build
```

In case you need super user access, prefix 'sudo -E' to above command.

***NOTE :*** To support OnDie ECDSA Device attestation, copy the required certificates and crls to tomcat/db/ondiecache folder.

# Running SDO Client

After All-In-One demo is initialized, use appropriate SDO Clients for demonstration.

The All-In-One demo listens at port 8080 for all incoming requests. You should modify SDO Client
configurations to use port 8080 for DI.

## Executing PRI Device

This step assumes that either you have built the [PRI](https://github.com/secure-device-onboard/pri)
source or you have the binaries associated with PRI repo.

The All-In-One demo provides sample configuration files for running PRI Device instance against
All-In-One demo. The configuration file **application.properties.aio** and the execution script
**device-di-to** is available in utils/sample-device folder. **Copy these files** into the 'pri/demo/device'
folder within PRI repository and execute following command from there.

```
cd pri/demo/device
bash device-di-to
```

When the script 'device-di-to' is executed, the device executes DI and then subsequently TO1 and TO2
against the SDO services running within All-In-One demo.

After the script execution, the status of SDO Client execution is available in result.txt file.

# Configuring All-In-One Demo

All-In-One demo provides REST interfaces, which allows All-In-One demo administrator to update the
configuration parameters as well as upload/download vouchers and payload files.

While executing the REST calls, prefix http://{host-ip-address}:8080 to the REST APIs below.

The REST calls can be executed through Postman or equivalent tools.

- For the authenticated calls, we need to select password based authentication with default
credentials (username: 'aio', password: 'Sm9@wojk').
- For body of the message, use 'Binary' format wherever we need to upload a file.

| Operation | REST API                      | Auth?  | Description                                                                    |
| ---------:|:-----------------------------:|:------:|:------------------------------------------------------------------------------:|
| `PUT`     | /api/v1/uploads/{file}        | YES    | Upload generic files used by All-In-One demo.                                  |
| `DELETE`  | /api/v1/uploads/{file}        | YES    | Delete uploaded files.                                                         |
| `GET`     | /api/v1/files/{file}          | NO     | Get files uploaded through /api/v1/uploads/{file}                              |
| `PUT`     | /api/v1/devices/{guid}/{file} | YES    | Upload {file} (voucher/configuration file) for device corresponding to {guid}  |
| `DELETE`  | /api/v1/devices/{guid}/{file} | YES    | Delete {file} (voucher/configuration file) for device corresponding to {guid}  |
| `GET`     | /api/v1/devices/{guid}/{file} | YES    | Get {file} (voucher/configuration file) device corresponding to {guid}         |
| `PUT`     | /api/v1/values/{file}         | YES    | Upload the payload specified by {file}                                         |
| `DELETE`  | /api/v1/values/{file}         | YES    | Delete {file} from the values folder                                           |
| `GET`     | /api/v1/values/{file}         | YES    | Get the payload specified by {file}                                            |
| `GET`     | /api/v1/deviceinfo/{seconds}  | YES    | Serves the serial no. and GUID of the devices that completed DI in last `n` seconds |

Following is the list of REST response error codes and it's description :

|     Error Code     |             Description                  |
| -------------------:|:----------------------------------------:|
| `401 Unauthorized`  | When an invalid Authentication header is present with the REST Request. Make sure to use the correct REST credentials. |
| `404 Not Found`     | When an invalid REST request is sent to AIO. Make sure to use the correct REST API endpoint. |
| `405 Method Not Allowed` | When an unsupported REST method is requested. Currently, AIO supports GET, PUT and DELETE only. |
| `406 Not Acceptable` | When an invalid filename is passed through the REST endpoints. |
| `500 Internal Server Error` | Due to internal error, AIO unable to fetch/copy/delete the requested file. |

## Configuring All-In-One Demo for Remote SDO Client

While executing a SDO Client from a different machine, you would need to change the IP address for
Rendezvous service. The configuration can be changed by updating redirect.properties file. The
example commands to perform the executions are provided below.

### Step-1: Create a redirect.properties File

Create a text file called 'redirect.properties' and copy the following contents.

```
# DNS of the Owner Protocol Service.
dns={host-ip-address}

# IP address of the Owner Protocol Service.
ip={host-ip-address}

# Port at which Owner Protocol Service is listening for incoming requests.
port=8080
```

The {host-ip-address} should be updated with the actual IP address of the host machine.

### Step-2: Upload redirect.properties File

Upload the generated redirect.properties file using following REST API call.

```
PUT http://{host-ip-address}:8080/api/v1/values/redirect.properties
```
## Configuring All-In-One Demo for external TLS Communications

By default, the all-in-one uses HTTP for all communications on port 8080.   In addition, the all on one can be configured to handle HTTPS on port 8443.

## Setup-1: Create or import a webserver certificate into a Java Keystore (JKS)

Ensure the web certificate is issued to the resolvable domain of the AIO server.

## Step-2: Uncomment and edit the 8443 Http11NioProtocol connector in tomcat/conf/server.xml
 <!-- Define an SSL/TLS HTTP/1.1 Connector on port 8443
         This connector uses the NIO implementation. The default
         SSLImplementation will depend on the presence of the APR/native
         library and the useOpenSSL attribute of the
         AprLifecycleListener.
         Either JSSE or OpenSSL style configuration may be used regardless of
         the SSLImplementation selected. JSSE style configuration is used below.
    -->

    <Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
               maxThreads="150" 
		scheme="https" secure="true" SSLEnabled="true"
            keystoreFile="<Path to JKS File>" 
            keystorePass="<JKS Store Password>"
            clientAuth="false" sslProtocol="TLS">
    </Connector>

## Step-3: Check TLS access of owner using heath check

Using browser or curl or wget
https://<FQDN>:8443/ops/mp/113/health

## Configure Owner to redirect to https port

Edit tomcat/db/v1/values/redirect.properties

port=8443

## Configure supply chain to use https for rendezvous

Edit tomcat/conf/catalina.properties

org.sdo.rv.scheme=https://

## Configure Relay servlet to use trust store

Edit tomcat/conf/catalina.properties

Add web server cert to client trust stores
server.ssl.trust-store=<Path to JKS>
server.ssl.trust-store-password=<Password of JKS>
server.ssl.trust-store-type=JKS

If using a test or internal certificate not issues by a web authority then add the cert into the Java CA Store
keytool -export -alias tomcat -file tomcat.crt -keystore <YourWebJSK>.jks
keytool -import -trustcacerts -noprompt -keystore "$JAVA_HOME\cacerts" -storepass changeit -alias tomcat -file tomcat.crt

## Configure All-In-One Demo for HTTPS with docker

Add the line below to the Dockerfile in order to add the cert to the java certs of the container.
RUN keytool -import -trustcacerts -noprompt -keystore "/usr/lib/jvm/java-11-openjdk-amd64/lib/security/cacerts" -storepass changeit -alias tomcat -file <PATH_TO_CERT>

docker-compose.yaml
extra_hosts:
- "<Domain_Name_in_Cert>:127.0.0.1"

## Configure All-In-One Demo for internal HTTPS communications (Optional)

Edit tomcat/conf/catalina.properties
rest.api.server=https://<FQDN>:8443/

Add web server cert to client trust stores
server.ssl.trust-store=<Path to JKS>
server.ssl.trust-store-password=<Password of JKS>
server.ssl.trust-store-type=JKS
client.ssl.trust-store=<Path to JKS>
client.ssl.trust-store-password=<Password of JKS>
client.ssl.trust-store-type=JKS

