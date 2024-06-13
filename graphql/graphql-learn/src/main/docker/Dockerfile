FROM openjdk:21-bullseye
MAINTAINER mai

RUN apt-get update \
  && apt-get install -y \
    wget \
    unzip \
    tcpdump \
    dnsutils \
    net-tools \
    procps \
  && apt-get autoremove \
  && apt-get autoclean \
  && rm -rf /var/lib/apt/lists/*
ENV JAVA_OPTS=""

RUN mkdir -p /opt/mai
RUN cd /opt/mai \
  && wget -q http://repo.hq.telesens.lan/nexus/service/local/repositories/thirdparty/content/ua/telesens/o320/trt/fe/distrib/converter-utils/3.0.1.1/converter-utils-3.0.1.1.tgz \
  && tar xfz converter-utils-3.0.1.1.tgz \
  && rm -f converter-utils-3.0.1.1.tgz
ADD src/main/scripts/*.sh /opt/mai/app/bin/
ADD target/dependency /opt/mai/app/lib
ADD target/tif-dependency /opt/mai/app/lib
ADD target/be-dependency /opt/mai/app/lib
ADD target/graphql-*.jar /opt/mai/app/lib

RUN chmod +x /opt/mai/app/bin/*

WORKDIR  /opt/mai/app/bin/
ADD src/main/docker/entrypoint.sh entrypoint.sh
RUN chmod a+x entrypoint.sh
ENTRYPOINT ["/opt/mai/app/bin/entrypoint.sh"]

ARG appVersion
ARG gitcommithash

LABEL "AppVersion"=$appVersion
LABEL "GitCommitIdFull"=$gitcommithash