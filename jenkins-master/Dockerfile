FROM jenkins/jenkins:lts

USER root
RUN apt-get update && apt-get install -qq -y --no-install-recommends \
git \
python \
ca-certificates \
wget

USER jenkins

RUN git clone https://github.com/sap/SapMachine-infrastructure /tmp/SapMachine-infrastructure
WORKDIR /tmp/SapMachine-infrastructure
RUN python /tmp/jenkins_restore.py -s /tmp/SapMachine-infrastructure -t /var/jenkins_home --install-plugins
RUN rm -rf /tmp/SapMachine-infrastructure

WORKDIR /

COPY init-sapmachine.groovy /usr/share/jenkins/ref/init.groovy.d/init-sapmachine.groovy

ENV JAVA_OPTS=-Djenkins.install.runSetupWizard=false
