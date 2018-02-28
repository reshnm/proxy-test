#!/bin/sh

if [ -d sapmachine ]; then
    rm -rf sapmachine
fi

mkdir sapmachine
cp -r compose.yml sapmachine
cp -r ci sapmachine
cp -r dist sapmachine
cp -r redirect sapmachine

cd sapmachine
docker-compose -f ./compose.yml up -d
