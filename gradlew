#!/bin/sh
APP_HOME=$(dirname "$(readlink -f "$0" 2>/dev/null || echo "$0")")
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
if [ -n "$JAVA_HOME" ]; then
  JAVA_EXE="$JAVA_HOME/bin/java"
else
  JAVA_EXE="java"
fi
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
exec "$JAVA_EXE" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
  "-Dorg.gradle.appname=$(basename "$0")" \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain "$@"
