#!/usr/bin/env bash
if [  ! -f target/got-records-0.1.0-SNAPSHOT-standalone.jar  ]; then
    lein uberjar
fi

java -jar -Dhttp.port=8888 target/got-records-0.1.0-SNAPSHOT-standalone.jar