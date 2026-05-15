#!/bin/sh
#
# Gradle wrapper script — uses locally extracted Gradle 8.11.1 binary.
# Fabric Loom 1.9-SNAPSHOT requires Gradle 8.11+.
#
GRADLE_HOME="/tmp/gradle-8.11"
exec "$GRADLE_HOME/bin/gradle" "$@"
