#!/usr/bin/env bash
VERSION="${VERSION:-latest}"
PUSH="${PUSH:-true}"
docker build -t lanchonete .
if [ "$PUSH" = true ]; then
  docker tag lanchonete kschltz/lanchonete:${VERSION}
  docker push kschltz/lanchonete:${VERSION}
fi
