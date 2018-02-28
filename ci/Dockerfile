FROM jenkins/jenkins:lts

USER root
RUN apt-get update && apt-get install -qq -y --no-install-recommends \
git \
python \
ca-certificates \
wget


USER jenkins
RUN git clone https://github.com/sap/SapMachine-infrastructure /tmp/SapMachine-infrastructure
RUN python /tmp/SapMachine-infrastructure/lib/jenkins_restore.py -s /tmp/SapMachine-infrastructure -t /var/jenkins_home --install-plugins
RUN rm -rf /tmp/SapMachine-infrastructure

COPY init-sapmachine.groovy /usr/share/jenkins/ref/init.groovy.d/init-sapmachine.groovy


USER root
RUN mkdir /var/pkg

RUN mkdir -p /var/pkg/deb/amd64
RUN mkdir -p /var/pkg/deb/keys
RUN mkdir -p /var/pkg/apk/3.5/x86_64
RUN mkdir -p /var/pkg/apk/keys

COPY keys/debian/* /var/pkg/deb/keys/
COPY keys/alpine/* /var/pkg/apk/keys/

RUN chown -R jenkins:jenkins /var/pkg


USER jenkins
ENV JAVA_OPTS=-Djenkins.install.runSetupWizard=false
VOLUME /var/pkg
WORKDIR /var/jenkins_home