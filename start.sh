#!/bin/sh

if [ -d sapmachine ]; then
    rm -rf sapmachine
fi

mdkir sapmachine
cp -r compose.yml sapmachine
cp -r ci sapmachine
cp -r dist sapmachine

cd sapmachine
docker-compose -f ./compose.yml up -d
