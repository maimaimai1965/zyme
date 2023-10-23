SET DB_HOST=localhost
SET DB_PORT=3309
SET DB_NAME=zyme02
SET TZ=Europe/Kiev
SET CONNECT_TIMEOUT_MS=20000
SET MAX_RECONNECTS=2
SET DB_USER=root
SET DB_PASS=root
SET ENCODING=utf8
SET SCRIPT.ENCODING=utf8
SET not_prod=true
SET DB_DRIVER_CP=D:\Install\Java\JDBC\MySQL\mysql-connector\mysql-connector-java-8.0.21.jar

ant -f dbsetup.xml install-test  > _install-test.log
rem ant -f dbsetup.xml install-test
