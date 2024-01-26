#!/usr/bin/env bash

PUSH="${PUSH:-false}"

docker build -t lanchonete .
docker tag lanchonete kschltz/fiap-pos:${VERSION}
if [ "$PUSH" = true ] ; then
  docker push kschltz/fiap-pos:${VERSION}
fi