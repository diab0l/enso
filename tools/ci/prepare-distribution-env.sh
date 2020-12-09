#!/usr/bin/env bash
DIST_ARCH=amd64
LAUNCHER_DIST_ROOT=enso-launcher-$DIST_VERSION-$DIST_OS-$DIST_ARCH
LAUNCHER_DIST_DIR=$LAUNCHER_DIST_ROOT/enso
ENGINE_DIST_ROOT=enso-engine-$DIST_VERSION-$DIST_OS-$DIST_ARCH
ENGINE_DIST_DIR=$ENGINE_DIST_ROOT/enso-$DIST_VERSION
PROJECTMANAGER_DIST_ROOT=enso-project-manager-$DIST_VERSION-$DIST_OS-$DIST_ARCH
PROJECTMANAGER_DIST_DIR=$PROJECTMANAGER_DIST_ROOT/enso
echo "LAUNCHER_DIST_DIR=$LAUNCHER_DIST_DIR" >> $GITHUB_ENV
echo "LAUNCHER_DIST_ROOT=$LAUNCHER_DIST_ROOT" >> $GITHUB_ENV
echo "ENGINE_DIST_DIR=$ENGINE_DIST_DIR" >> $GITHUB_ENV
echo "ENGINE_DIST_ROOT=$ENGINE_DIST_ROOT" >> $GITHUB_ENV
echo "PROJECTMANAGER_DIST_DIR=$PROJECTMANAGER_DIST_DIR" >> $GITHUB_ENV
echo "PROJECTMANAGER_DIST_ROOT=$PROJECTMANAGER_DIST_ROOT" >> $GITHUB_ENV
