FROM openjdk:21-bullseye
MAINTAINER mai

RUN apt-get update \
  && apt-get install -y \
    wget \
    mc \
    netcat
#    net-tools
#    unzip \
#    tcpdump \
#    dnsutils \
#    procps \
#  && apt-get autoremove \
#  && apt-get autoclean \
#  && rm -rf /var/lib/apt/lists/*
#ENV JAVA_OPTS=""

RUN mkdir -p /opt/zyme/r2dbc-mysql/bin/
#RUN chmod +x /opt/zyme/r2dbc-mysql/bin/*

ADD target/r2dbc-mysql-be-app-*.jar /opt/zyme/r2dbc-mysql/bin
#RUN cd /opt/trt \
#  && wget -q http://repo.hq.telesens.lan/nexus/service/local/repositories/thirdparty/content/ua/telesens/o320/trt/fe/distrib/converter-utils/3.0.1.1/converter-utils-3.0.1.1.tgz \
#  && tar xfz converter-utils-3.0.1.1.tgz \
#  && rm -f converter-utils-3.0.1.1.tgz
#ADD src/main/scripts/*.sh /opt/trt/app/bin/
#ADD target/dependency /opt/trt/app/lib
#ADD target/tif-dependency /opt/trt/app/lib
#ADD target/be-dependency /opt/trt/app/lib
#ADD target/bootstrap-*.jar /opt/trt/app/lib

#ENV SPRING_R2DBC_URL="jdbc:mysql://172.19.0.2:3306/trt?autoReconnect=true&maxReconnects=2&connectTimeout=100&serverTimezone=Europe/Kiev"
#ENV SPRING_R2DBC_URL="jdbc:mysql://trt-lifecell-ua_setup_db-mysql-1:3306/trt"

WORKDIR /opt/zyme/r2dbc-mysql/bin/
#ADD src/main/docker/entrypoint.sh entrypoint.sh
#RUN chmod a+x entrypoint.sh
#ENTRYPOINT ["/opt/zyme/r2dbc-mysql/bin/entrypoint.sh"]
#CMD ["java -jar r2dbc-mysql-be-app-0.0.1-SNAPSHOT.jar"]
#CMD ["java -jar /opt/zyme/r2dbc-mysql/bin/r2dbc-mysql-be-app-0.0.1-SNAPSHOT.jar"]
#CMD ["java", "-jar", "/opt/zyme/r2dbc-mysql/bin/r2dbc-mysql-be-app-0.0.1-SNAPSHOT.jar"]
CMD ["/bin/sh"]


ARG appVersion
ARG gitcommithash

LABEL "AppVersion"=0.0.1-SNAPSHOT
#LABEL "AppVersion"=$appVersion
#LABEL "GitCommitIdFull"=$gitcommithash