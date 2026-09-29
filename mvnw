#!/bin/sh
set -eu

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
PROPERTIES_FILE="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"
DISTRIBUTION_URL=$(sed -n 's/^distributionUrl=//p' "$PROPERTIES_FILE")

if [ -z "$DISTRIBUTION_URL" ]; then
    echo "distributionUrl is missing from $PROPERTIES_FILE" >&2
    exit 1
fi

ARCHIVE_NAME=${DISTRIBUTION_URL##*/}
MAVEN_DIR=${ARCHIVE_NAME%-bin.zip}
CACHE_ROOT="${HOME:?HOME must be set}/.m2/wrapper/dists"
MAVEN_HOME="$CACHE_ROOT/$MAVEN_DIR"

if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
    mkdir -p "$CACHE_ROOT"
    TEMP_DIR=$(mktemp -d "$CACHE_ROOT/.maven-wrapper.XXXXXX")
    trap 'rm -rf "$TEMP_DIR"' EXIT HUP INT TERM

    if command -v curl >/dev/null 2>&1; then
        curl -fsSL "$DISTRIBUTION_URL" -o "$TEMP_DIR/$ARCHIVE_NAME"
    elif command -v wget >/dev/null 2>&1; then
        wget -q "$DISTRIBUTION_URL" -O "$TEMP_DIR/$ARCHIVE_NAME"
    else
        echo "curl or wget is required to download Maven" >&2
        exit 1
    fi

    unzip -q "$TEMP_DIR/$ARCHIVE_NAME" -d "$TEMP_DIR"
    if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
        mv "$TEMP_DIR/$MAVEN_DIR" "$MAVEN_HOME"
    fi
    rm -rf "$TEMP_DIR"
    trap - EXIT HUP INT TERM
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
