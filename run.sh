#!/usr/bin/env bash
# ==============================================================================
# Smart Campus Complaint Management System - Run Script
# VIT Bhopal University - Java Academic Project
# ==============================================================================

set -e

# Detect Java Runtime
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
elif [ -x "$HOME/.jdk/current/bin/java" ]; then
    JAVA_CMD="$HOME/.jdk/current/bin/java"
elif command -v java >/dev/null 2>&1; then
    JAVA_CMD="java"
else
    echo "ERROR: java runtime not found in PATH or ~/.jdk/current."
    echo "Please set JAVA_HOME or install Java 17+."
    exit 1
fi

# Ensure compiled classes exist
if [ ! -f "bin/main/Main.class" ]; then
    echo "Binaries not found. Running compilation first..."
    ./compile.sh
fi

echo "=================================================================="
echo "Launching Smart Campus Complaint Management System..."
echo "Runtime: $($JAVA_CMD -version 2>&1 | head -n 1)"
echo "=================================================================="

exec $JAVA_CMD -cp bin main.Main "$@"
