#!/usr/bin/env bash
BUILD="${BUILD:-false}"
alias kubectl='minikube kubectl --'
minikube delete
minikube start
wait
if [ "$BUILD" = true ]; then
  minikube image build -t lanchonete .
fi
wait
minikube kubectl -- apply -f ./k8s/postgre/
minikube kubectl -- apply -f ./k8s/lanchonete/
minikube kubectl -- logs -l app=lanchonete -f
minikube addons enable metrics-server
minikube tunnel
