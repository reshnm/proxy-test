#!/bin/sh

DOMAIN=$1
DOCKER_IMAGE=$2
NAME=$3

docker run -d --name ${NAME} --network=le_ext -e "LETSENCRYPT_HOST=${DOMAIN}" -e "LETSENCRYPT_EMAIL=sapmachine@sap.com" -e "VIRTUAL_HOST=${DOMAINT}" ${DOCKER_IMAGE}
