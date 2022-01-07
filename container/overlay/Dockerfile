#
# Build docker container for Data Access Service
#
#
FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get -y update && apt-get -y upgrade && apt-get -y install openjdk-11-jre-headless haveged

RUN useradd -ms /bin/bash aio
USER aio

RUN mkdir -p /home/aio/demo
WORKDIR /home/aio/demo

COPY --chown=aio:aio ./tomcat /home/aio/demo
RUN chmod ugo+rwx /home/aio/demo

#Uncomment the below section to enable HTTPS support. Update the JVM and certificate path.
#USER root
#RUN keytool -import -trustcacerts -noprompt -keystore "/usr/lib/jvm/java-11-openjdk-amd64/lib/security/cacerts" -storepass changeit -alias tomcat -file /home/aio/demo/db/tls.crt
#USER aio

CMD sh /home/aio/demo/bin/catalina.sh run
