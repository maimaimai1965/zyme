SET DB_HOST=localhost
SET DB_PORT=3316
SET DB_USER=root
SET DB_PASS=root
SET DB_SCHEMA=zyme
SET DB_SCHEMA_USER=zyme
SET DB_SCHEMA_PASS=zyme

SET TZ=Europe/Kiev
SET CONNECT_TIMEOUT_MS=20000
SET MAX_RECONNECTS=2
SET ENCODING=utf8
SET SCRIPT.ENCODING=utf8
SET not_prod=true
SET DB_DRIVER_CP=C:/work/idea_home/zyme/db/lib/mysql-connector-java-8.0.28.jar

ant -f dbsetup.xml install
rem ant -f dbsetup.xml install  > _install.log
rem ant -f dbsetup.xml install-test  > _install-test.log
rem ant -f dbsetup.xml install-test
