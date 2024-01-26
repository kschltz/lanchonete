#!/bin/bash
alias kubectl='minikube kubectl --' &&
  minikube delete &&
  minikube start

CREDS_PATH=~/.docker/config.json bash ./bin/docker-secrets.sh &&
  minikube kubectl -- apply -f ./k8s/postgre/ &&
  minikube kubectl -- apply -f ./k8s/postgre/ &&
  minikube kubectl -- apply -f ./k8s/lanchonete/ &&
  minikube kubectl -- logs -l app=lanchonete -f  && minikube addons enable metrics-server
