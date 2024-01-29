#!/usr/bin/env bash

PUSH="${PUSH:-false}"

docker build -t lanchonete .
if [ "$PUSH" = true ] ; then
  docker tag lanchonete kschltz/fiap-pos:${VERSION}
  docker push kschltz/fiap-pos:${VERSION}
fi