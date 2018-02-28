FROM nginx:latest

RUN rm -rf /var/www/*
RUN mkdir -p /var/www/alpine
RUN mkdir -p /var/www/debian

RUN ln -s /var/pkg/deb/amd64 /var/www/debian/amd64
RUN ln -s /var/pkg/deb/keys/sapmachine.key /var/www/debian/sapmachine.key

RUN ln -s /var/pkg/apk/3.5 /var/www/alpine/3.5
RUN ln -s `ls /var/pkg/apk/keys/*.rsa.pub`

VOLUME /var/www
