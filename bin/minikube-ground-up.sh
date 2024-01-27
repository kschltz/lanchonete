#!/usr/bin/env bash
VERSION=latest bash ./bin/release-docker.sh
alias kubectl='minikube kubectl --'
minikube delete
minikube start
minikube image load lanchonete
wait
CREDS_PATH=~/.docker/config.json bash ./bin/docker-secrets.sh
minikube kubectl -- apply -f ./k8s/postgre/
minikube kubectl -- apply -f ./k8s/lanchonete/
minikube kubectl -- logs -l app=lanchonete -f
minikube addons enable metrics-server

minikube tunnel
