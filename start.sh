#!/bin/sh

if [ -d sapmachine ]; then
    rm -rf sapmachine
fi

mkdir sapmachine
cp -r compose.yml sapmachine
cp -r .env sapmachine
cp -r ci sapmachine
cp -r dist sapmachine
cp -r redirect sapmachine
cp -r ci-slave-ubuntu sapmachine

cd sapmachine
docker-compose -f ./compose.yml up -d $1
