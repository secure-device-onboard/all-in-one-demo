#!/bin/sh
# Copyright 2020 Intel Corporation
# SPDX-License-Identifier: Apache 2.0

SSL_KEY_STORE="rendezvous-keystore.jks"
JAVA_SSL_PARAMS="-Dserver.ssl.key-store=/home/sdouser/certs/$SSL_KEY_STORE -Dserver.ssl.key-store-password=$SSL_KEY_STORE_PASSWORD"
JAVA_REDIS_PARAMS="-Dredis.host=$REDIS_HOST -Dredis.password=$REDIS_PASSWORD -Dredis.port=$REDIS_PORT"
TRUST_STORE_SSL_PARAM="-Djavax.net.ssl.trustStore=/home/sdouser/certs/rendezvous-trusterRootCA.jks -Djavax.net.ssl.trustStorePassword=$SSL_TRUST_STORE_PASSWORD"

# This is an example implementation. This should be updated in production deployment.
# Care must be taken to ensure the cryptographic strength of this key.
HMAC_SECRET="-Drendezvous.hmacSecret=$(openssl rand -hex 64)"

PROXY_SETTINGS=""
if [ "" != "${http_proxy}" ]
then
    NO_SCHEMA_HTTP_ADDRESS=$(echo ${http_proxy} | sed -r 's#^(.*://)##')
    HTTP_PROXY_URL=$(echo ${NO_SCHEMA_HTTP_ADDRESS} | sed -r 's#:.*##')
    HTTP_PROXY_PORT=$(echo ${NO_SCHEMA_HTTP_ADDRESS} | sed -r 's#.*:##')
    PROXY_SETTINGS="-Dhttp.proxyHost=${HTTP_PROXY_URL} -Dhttp.proxyPort=${HTTP_PROXY_PORT}"
fi
if [ "" != "${https_proxy}" ]
then
    NO_SCHEMA_HTTPS_ADDRESS=$(echo ${https_proxy} | sed -r 's#^(.*://)##')
    HTTPS_PROXY_URL=$(echo ${NO_SCHEMA_HTTPS_ADDRESS} | sed -r 's#:.*##')
    HTTPS_PROXY_PORT=$(echo ${NO_SCHEMA_HTTPS_ADDRESS} | sed -r 's#.*:##')
    PROXY_SETTINGS="${PROXY_SETTINGS} -Dhttps.proxyHost=${HTTPS_PROXY_URL} -Dhttps.proxyPort=${HTTPS_PROXY_PORT}"
fi

java ${PROXY_SETTINGS} ${JAVA_REDIS_PARAMS} ${TRUST_STORE_SSL_PARAM} ${JAVA_SSL_PARAMS} ${HMAC_SECRET} -jar /home/sdouser/rendezvous-service-*.war
