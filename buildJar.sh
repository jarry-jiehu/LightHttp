#!/bin/sh
rm -rf target/*
./gradlew clean
./gradlew okio:okIoRelease copyJar --stacktrace
./gradlew okhttp3:okHttpRelease copyJar --stacktrace
./gradlew lighthttplib:lightHttpRelease copyJar --stacktrace
./gradlew lighthttplib:zipJavadoc --stacktrace
