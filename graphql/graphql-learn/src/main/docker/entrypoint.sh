#!/bin/sh
#
# ---------------------------------------------------------------------
# Startup script.
# ---------------------------------------------------------------------
#

# ---------------------------------------------------------------------
# Ensure application points to the directory where the application is installed.
# ---------------------------------------------------------------------
SCRIPT_LOCATION=$0
if [ -x "$READLINK" ]; then
  while [ -L "$SCRIPT_LOCATION" ]; do
    SCRIPT_LOCATION=`"$READLINK" -e "$SCRIPT_LOCATION"`
  done
fi

cd "`dirname "$SCRIPT_LOCATION"`"
BIN_DIR=`pwd`
HOME_DIR=`dirname "$BIN_DIR"`
cd "$OLDPWD"

# ---------------------------------------------------------------------
# Setup the JVM
# ---------------------------------------------------------------------
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
	JAVA="$JAVA_HOME/bin/java"
    else
	JAVA="java"
    fi
fi
# ---------------------------------------------------------------------
# Collect JVM options and IDE properties.
# ---------------------------------------------------------------------
APP_PROPERTIES="--add-opens java.base/java.lang=ALL-UNNAMED"
COMMON_JVM_ARGS=${JAVA_OPTS}
ALL_JVM_ARGS="$APP_PROPERTIES $COMMON_JVM_ARGS"

n=${APP_NAME}
MAIN_CLASS="ua.telesens.o320.bootstrap.trt.be"

if [ "$n" = "core" ]; then
    MAIN_CLASS="${MAIN_CLASS}.TrtCoreApplication"
elif [ "$n" = "ui" ]; then
    MAIN_CLASS="${MAIN_CLASS}.TrtUiApplication"
elif [ "$n" = "external-ui" ]; then
    MAIN_CLASS="${MAIN_CLASS}.TrtExternalUiApplication"
elif [ "$n" = "sim" ]; then
    MAIN_CLASS="${MAIN_CLASS}.TrtIntegrationApplication"
fi

for file in ${HOME_DIR}/lib/*.jar
    do
        if [ -z "$CLASSPATH" ]; then
            CLASSPATH="$file"
        else
            CLASSPATH="$CLASSPATH:$file"
        fi
    done

# ---------------------------------------------------------------------
# Run the application.
# ---------------------------------------------------------------------
exec "$JAVA" ${ALL_JVM_ARGS} -classpath "${CLASSPATH}" ${MAIN_CLASS} "$@"