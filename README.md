# ChaosMonkeyPod

## Description
Let you delete a random pod in a namespace

## Installation
```
docker build . -t riccardo-bottini/chaosmonkey

kubectl create namespace testing
kubectl create deployment deploytarget --image=nginx --replicas=4 -n=testing

kubectl apply -f /manifest/k8s-deployment.yaml
```