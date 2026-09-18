#!/usr/bin/env bash
# ==============================================================================
# Smart Campus Complaint Management System - Compilation Script
# VIT Bhopal University - Java Academic Project
# ==============================================================================

set -e

# Detect Java Runtime
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    JAVAC_CMD="$JAVA_HOME/bin/javac"
elif [ -x "$HOME/.jdk/current/bin/javac" ]; then
    JAVAC_CMD="$HOME/.jdk/current/bin/javac"
elif command -v javac >/dev/null 2>&1; then
    JAVAC_CMD="javac"
else
    echo "ERROR: javac compiler not found in PATH or ~/.jdk/current."
    echo "Please set JAVA_HOME or install Java 17+."
    exit 1
fi

echo "=================================================================="
echo "Compiling Smart Campus Complaint Management System..."
echo "Compiler: $($JAVAC_CMD -version 2>&1)"
echo "=================================================================="

mkdir -p bin
mkdir -p data

# Compile application sources
$JAVAC_CMD -d bin -sourcepath src src/main/Main.java

# Compile test suite
$JAVAC_CMD -d bin -cp bin tests/*.java

echo "Compilation successful! All class files generated in 'bin/' directory."
