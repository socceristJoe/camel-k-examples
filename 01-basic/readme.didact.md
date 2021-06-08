# Camel K Basic Example



## Available online



## Before you begin



## Requirements



**Kubectl CLI**



**Connection to a Kubernetes cluster**



**Apache Camel K CLI ("kamel")**

You need the Apache Camel K CLI ("kamel") in order to access all Camel K features.

```
Download Camel K
wget https://github.com/apache/camel-k/releases/download/v1.4.0/camel-k-client-1.4.0-linux-64bit.tar.gz

Uncompress binary file
mkdir /usr/bin/kamel && tar -xvf camel-k-client-1.4.0-linux-64bit.tar.gz -C /usr/local/bin/
```

```
Download Camel K
brew install kamel
```


## 1. Preparing the namespace

Let's open a terminal and go to the example directory:

We're going to create a namespace named `camel-basic` for running the example. To create it, execute the following command:

```
kubectl create namespace camel-basic
```


Now we can set the `camel-basic` namespace as default namespace for the following commands:

```
kubectl config set-context --current --namespace=camel-basic
```


You need to install Camel K in the `camel-basic` namespace (or globally in the whole cluster).
In many settings (e.g. OpenShift, CRC), it's sufficient to execute the following command to install Camel K:

## OLM install

install Operator lifecycle manager https://github.com/operator-framework/operator-lifecycle-manager
https://github.com/operator-framework/operator-lifecycle-manager/releases
```
kubectl apply -f https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.18.1/crds.yaml
kubectl apply -f https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.18.1/olm.yaml
```

## docker registry install
```
docker run -d -p 5000:5000 -v /opt/data/registry:/var/lib/registry docker.io/registry

docker tag registry:latest kamel/registry:joepo

vim /etc/docker/daemon.json

ADD 
{
  "exec-opts": ["native.cgroupdriver=systemd"],
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m"
  },
  "storage-driver": "overlay2",
  "insecure-registries": [
    "192.168.50.4:5000"
  ]
}

docker run -d -p 5000:5000 -v /opt/data/registry:/var/lib/registry kamel/registry:joepoc
```

## install camel-k with CRDs
基于临时 Registry：localhost:5000
配置 build 超时时间：1h 
原文链接：https://blog.csdn.net/wu_weijie/article/details/104892921
```
kamel install --base-image openjdk:11 --registry 192.168.50.4:5000 --registry-insecure --build-timeout 1h --save -n camel-basic

kamel install --base-image openjdk:11 --registry azrgeaipoc.azurecr.io --registry-auth-username $camelkacr --registry-auth-password $camelkacrsecret --build-timeout 1h --save -n camel-basic

```

## uninstall
```
kamel uninstall -n camel-basic
kubectl delete all,pvc,configmap,rolebindings,clusterrolebindings,secrets,sa,roles,clusterroles,crd -l 'app=camel-k' -n camel-k
```

NOTE: The `kamel install` command requires some prerequisites to be successful in some situations, e.g. you need to enable the registry addon on Minikube. Refer to the [Camel K install guide](https://camel.apache.org/camel-k/latest/installation/installation.html) for cluster-specific instructions.

## check crd status
To check that Camel K is installed we'll retrieve the IntegrationPlatform object from the namespace:

```
kubectl get integrationplatform
```

You should find an IntegrationPlatform in status `Ready`.

You can now proceed to the next section.

## 2. Running a basic integration

```
cd /Users/joeqiao/Documents/LocalHub/camel/camel-k-examples/01-basic


cd /vagrant/tmp
kamel run Basic.java -n camel-basic --dev
```

To keep the integration running and not linked to the terminal, you can run it without "dev mode", just run:

```
kamel run Basic.java -n camel-basic
```
Even if it's not running in dev mode, you can still see the logs of the integration using the following command:

```
kamel log basic
```

## 2. Applying configuration and routing

The routes use two configuration properties named `items` and `priority-marker` that should be provided using an external file such
as the `routing.properties` 

The `Routing.java` file shows how to inject properties into the routes via property placeholders and also the usage of the `@PropertyInject` annotation.

```
kamel run Routing.java -n camel-basic --property-file routing.properties --dev
```
Change the word `door` with `*door` to see it sent to the priority queue.

## 3. Running integrations as Kubernetes CronJobs

The previous example can be automatically deployed as a Kubernetes CronJob if the delay between executions is changed into a value that can be expressed by a cron tab expression.

For example, you can change the first endpoint (`timer:java?period=3000`) into the following: `timer:java?period=60000` (1 minute between executions).

Now you can run the integration again:

```
kamel run Routing.java -n camel-basic --property-file routing.properties  --dev
```
Now you'll see that Camel K has materialized a cron job:

```
kubectl get cronjob
kamel delete routing -n camel-basic 
```
You can see the pods starting and being destroyed by watching the namespace:

```
kubectl -n camel-basic get pod -w
```
To see the logs of each integration starting up, you can use the `kamel log` command:

```
kamel log routing -n camel-basic 
```

You should see every minute a JVM starting, executing a single operation and terminating.


The CronJob behavior is controlled via a Trait called `cron`. Traits are the main way to configure high level Camel K features, to 
customize how integrations are rendered.

To disable the cron feature and use the deployment strategy, you can run the integration with:

```
kamel -n camel-basic run Routing.java --property-file routing.properties -t cron.enabled=false --dev
```

This will disable the cron trait and restore the classic behavior (always running pod).

You should see it reflected in the logs (which will be printed every minute by the same JVM):

```
kamel log routing -n camel-basic 
```

## 4. Uninstall

To cleanup everything, execute the following command:

```kubectl delete namespace camel-basic```
