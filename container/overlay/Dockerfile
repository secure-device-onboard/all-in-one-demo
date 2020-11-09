#
# Build docker container for Data Access Service
#
#
FROM ubuntu:20.04

RUN apt-get update && apt-get install openjdk-11-jre-headless --yes

RUN useradd -ms /bin/bash aio
USER aio

RUN mkdir -p /home/aio/demo
WORKDIR /home/aio/demo

COPY --chown=aio:aio ./tomcat /home/aio/demo
RUN chmod ugo+rwx /home/aio/demo

CMD sh /home/aio/demo/bin/catalina.sh run
