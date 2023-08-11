# ChaosMonkeyPod

## Description
Let you delete a random pod in a namespace

## Requirements
* JDK 17
* Maven 3.5.2
* Docker and a local Kubernetes cluster running on your machine

## Installation and how to run
```
mvn clean package

kubectl create namespace testing
kubectl create deployment deploytarget --image=nginx --replicas=4 -n=testing

kubectl apply -f manifest/k8s-testing-role.yaml
kubectl apply -f manifest/k8s-deployment.yaml
```
## Settings
You can set the target namespace, the fixed rate scheduler and the initial delay in the configMap in manifest/k8s-deployment.yaml.  
If you edit the target namespace you have to create a new workload in the selected ns.  

#
I'm not responsible of any errors in your cluster caused by a pod deletion made with this app ;D
