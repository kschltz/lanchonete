#!/bin/bash
kubectl create secret generic regcred --from-file=.dockerconfigjson=$CREDS_PATH --type=kubernetes.io/dockerconfigjson