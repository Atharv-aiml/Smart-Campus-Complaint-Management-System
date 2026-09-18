#!/usr/bin/env bash
# ==============================================================================
# Smart Campus Complaint Management System - Test Execution Script
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

# Ensure test binaries exist
if [ ! -f "bin/tests/TestSuiteRunner.class" ]; then
    echo "Compiling tests first..."
    ./compile.sh
fi

exec $JAVA_CMD -cp bin tests.TestSuiteRunner "$@"
