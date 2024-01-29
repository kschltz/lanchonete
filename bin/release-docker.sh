#!/usr/bin/env bash

PUSH="${PUSH:-false}"
docker build -t lanchonete .
if [ "$PUSH" = true ] ; then
  docker tag lanchonete kschltz/lanchonete:${VERSION}
  docker push kschltz/lanchonete:${VERSION}
fi