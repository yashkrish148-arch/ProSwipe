#!/bin/sh
if [ -z "$JAVA_HOME" ]; then JAVACMD="java"; else JAVACMD="$JAVA_HOME/bin/java"; fi
WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"
exec "$JAVACMD" $MAVEN_OPTS -classpath "$WRAPPER_JAR" "-Dmaven.multiModuleProjectDirectory=$PWD" org.apache.maven.wrapper.MavenWrapperMain "$@"
