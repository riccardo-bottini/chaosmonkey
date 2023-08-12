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
kubectl create deployment deploymenttesting1 --image=nginx --replicas=4 -n=testing
kubectl create deployment deploydeleteme --image=nginx --replicas=3 -n testing

kubectl apply -f manifest/k8s-testing-role.yaml
kubectl apply -f manifest/k8s-chaosmonkey-deploy.yaml
```
## Settings
By default, the application removes a random pod in testing namespace. If you want to delete a pod of a specific deployment or a single pod you created, you can edit the following properties in the config map in manifest/k8s-deployment.yaml:  
* target-label-key (for the label key)
* target-label-value (for the label value)  
---
For example, if you want to delete a random pod of the deployment "deploydeleteme", follow these steps:
1. target-label-key: "app" and target-label-value: "deploydeleteme"
2. Run these commands: 
    ```
    kubectl delete deploy chaosmonkeypod
    kubectl apply -f manifest/k8s-chaosmonkey-deploy.yaml
    ```
---   
You can also set the fixed rate interval and the initial delay of the scheduler in the same config map in the same way with the properties "scheduler-fixedrate" and "scheduler-initialdelay".  



I'm not responsible of any errors in your cluster caused by a pod deletion made with this app ;D
