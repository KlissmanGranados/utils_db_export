#!/bin/sh


if [ "$(ls -A ./keys/)" ]; then

    if lsof -ti :27018 > /dev/null; then
        kill $(lsof -ti :27018)
    fi

    if [ -n "$SSH_TUNNEL_COMMAND" ]; then
        eval "$SSH_TUNNEL_COMMAND"
    fi

fi

java -jar ./target/utils-0.0.1-SNAPSHOT.jar