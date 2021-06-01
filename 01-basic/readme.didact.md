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
指定运行 camel 的基础镜像：openjdk:8
————————————————
版权声明：本文为CSDN博主「wuweijie@apache.org」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/wu_weijie/article/details/104892921
```
kamel install

kamel install -n camel-basic --registry=https://index.docker.io/v1/

kamel install --base-image openjdk:11 --registry 192.168.50.4:5000 --registry-insecure --build-timeout 1h --save -n camel-basic

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

This repository contains a simple Camel K integration that periodically prints 
a "Hello World..." message.

> **Note:** the `Basic.java` file contains a simple integration that uses the `timer` and `log` components.
> Dependency management is automatically handled by Camel K that imports all required libraries from the Camel
> catalog via code inspection. This means you can use all 300+ Camel components directly in your routes.

We're ready to run the integration on our `camel-basic` project in the cluster.

Use the following command to run it in "dev mode", in order to see the logs in the integration terminal:

```
cd /Users/joeqiao/Documents/LocalHub/camel/camel-k-examples/01-basic


cd /vagrant/tmp
kamel run Basic.java -n camel-basic --dev
```

If everything is ok, after the build phase finishes, you should see the Camel integration running and continuously printing "Hello World!..." in the terminal window.

When running in dev mode, you can change the integration code and let Camel K redeploy the changes automatically.

To try this feature, and change "Hello World" into "Ciao Mondo", then save the file.
You should see the new integration starting up in the terminal window and replacing the old one.


> **Note:** When you terminate a "dev mode" execution, also the remote integration will be deleted. This gives the experience of a local program execution, but the integration is actually running in the remote cluster.

To keep the integration running and not linked to the terminal, you can run it without "dev mode", just run:

```
kamel run Basic.java
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20run%20Basic.java&completion=Camel%20K%20basic%20integration%20run. "Opens a new terminal and sends the command above"){.didact})



After executing the command, you should be able to see it among running integrations:

```
kubectl get integrations
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kubectl%20get%20integrations&completion=Getting%20running%20integrations. "Opens a new terminal and sends the command above"){.didact})

An integration named `basic` should be present in the list and it should be in status `Running`. There's also a `kamel get` command which is an alternative way to list all running integrations.

> **Note:** the first time you've run the integration, an IntegrationKit (basically, a container image) has been created for it and 
> it took some time for this phase to finish. When you run the integration a second time, the existing IntegrationKit is reused 
> (if possible) and the integration reaches the "Running" state much faster.
>


Even if it's not running in dev mode, you can still see the logs of the integration using the following command:

```
kamel log basic
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20log%20basic&completion=Show%20integration%20logs. "Opens a new terminal and sends the command above"){.didact})

The last parameter ("basic") is the name of the running integration for which you want to display the logs.

[**Click here to terminate the log stream**](didact://?commandId=vscode.didact.sendNamedTerminalCtrlC&text=camelTerm&completion=Camel%20K%20basic%20integration%20interrupted. "Interrupt the current operation on the terminal"){.didact} 
or hit `ctrl+c` on the terminal window.

> **Note:** Your IDE may provide an "Apache Camel K Integrations" panel where you can see the list of running integrations and also open a window to display the logs.


## 2. Applying configuration and routing

The second example is a bit more complex as it shows how to configure the integration using external properties and 
also a simple content-based router.

The integration is contained in a file named `Routing.java` ([open](didact://?commandId=vscode.openFolder&projectFilePath=01-basic/Routing.java&completion=Opened%20the%20Routing.java%20file "Opens the Routing.java file"){.didact}).

The routes use two configuration properties named `items` and `priority-marker` that should be provided using an external file such
as the `routing.properties` ([open](didact://?commandId=vscode.openFolder&projectFilePath=01-basic/routing.properties&completion=Opened%20the%20routing.properties%20file "Opens the routing.properties file"){.didact}).

The `Routing.java` file shows how to inject properties into the routes via property placeholders and also the usage of the `@PropertyInject` annotation.

To run the integration, we should link the integration to the property file providing configuration for it:

```
kamel run Routing.java --property-file routing.properties --dev
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20run%20Routing.java%20--property-file%20routing.properties%20--dev&completion=Run%20Routing.java%20integration. "Opens a new terminal and sends the command above"){.didact})

Wait for the integration to be running (you should see the logs streaming in the terminal window).

You can now open both the [Routing.java](didact://?commandId=vscode.openFolder&projectFilePath=01-basic/Routing.java&completion=Opened%20the%20Routing.java%20file "Opens the Routing.java file"){.didact} file or
the [routing.properties](didact://?commandId=vscode.openFolder&projectFilePath=01-basic/routing.properties&completion=Opened%20the%20routing.properties%20file "Opens the routing.properties file"){.didact}
file, make some changes and see the integration redeployed.
For example, change the word `door` with `*door` to see it sent to the priority queue.

[**Click here to exit dev mode and terminate the execution**](didact://?commandId=vscode.didact.sendNamedTerminalCtrlC&text=camelTerm&completion=Camel%20K%20basic%20integration%20interrupted. "Interrupt the current operation on the terminal"){.didact}, 
or hit `ctrl+c` on the terminal window.

This will also terminate the execution of the integration.

## 3. Running integrations as Kubernetes CronJobs

The previous example can be automatically deployed as a Kubernetes CronJob if the delay between executions is changed into a value that can be expressed by a cron tab expression.

For example, you can change the first endpoint (`timer:java?period=3000`) into the following: `timer:java?period=60000` (1 minute between executions). [Open the Routing.java file](didact://?commandId=vscode.openFolder&projectFilePath=01-basic/Routing.java&completion=Opened%20the%20Routing.java%20file "Opens the Routing.java file"){.didact} to apply the changes.

Now you can run the integration again:

```
kamel run Routing.java --property-file routing.properties
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20run%20Routing.java%20--property-file%20routing.properties&completion=Run%20Routing.java%20integration%20as%20CronJob. "Opens a new terminal and sends the command above"){.didact})

Now you'll see that Camel K has materialized a cron job:

```
kubectl get cronjob
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kubectl%20get%20cronjob&completion=Get%20CronJobs. "Opens a new terminal and sends the command above"){.didact})

You'll find a Kubernetes CronJob named "routing".

The running behavior changes, because now there's no pod always running (beware you should not store data in memory when using the cronJob strategy).

You can see the pods starting and being destroyed by watching the namespace:

```
kubectl get pod -w
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kubectl%20get%20pod%20-w&completion=Watch%20Pods. "Opens a new terminal and sends the command above"){.didact})

[**Click here to exit the current command**](didact://?commandId=vscode.didact.sendNamedTerminalCtrlC&text=camelTerm&completion=Camel%20K%20basic%20integration%20interrupted. "Interrupt the current operation on the terminal"){.didact},
or hit `ctrl+c` on the terminal window.

To see the logs of each integration starting up, you can use the `kamel log` command:

```
kamel log routing
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20log%20routing&completion=Watch%20integration%20logs. "Opens a new terminal and sends the command above"){.didact})

You should see every minute a JVM starting, executing a single operation and terminating.

[**Click here to exit the current command**](didact://?commandId=vscode.didact.sendNamedTerminalCtrlC&text=camelTerm&completion=Camel%20K%20basic%20integration%20interrupted. "Interrupt the current operation on the terminal"){.didact},
or hit `ctrl+c` on the terminal window.

The CronJob behavior is controlled via a Trait called `cron`. Traits are the main way to configure high level Camel K features, to 
customize how integrations are rendered.

To disable the cron feature and use the deployment strategy, you can run the integration with:

```
kamel run Routing.java --property-file routing.properties -t cron.enabled=false
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20run%20Routing.java%20--property-file%20routing.properties%20-t%20cron.enabled=false&completion=Run%20Routing.java%20integration%20without%20CronJobs. "Opens a new terminal and sends the command above"){.didact})

This will disable the cron trait and restore the classic behavior (always running pod).

You should see it reflected in the logs (which will be printed every minute by the same JVM):

```
kamel log routing
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20log%20routing&completion=Watch%20integration%20logs. "Opens a new terminal and sends the command above"){.didact})


[**Click here to exit the current command**](didact://?commandId=vscode.didact.sendNamedTerminalCtrlC&text=camelTerm&completion=Camel%20K%20basic%20integration%20interrupted. "Interrupt the current operation on the terminal"){.didact},
or hit `ctrl+c` on the terminal window.

You can continue to hack on the examples.

## 4. Uninstall

To cleanup everything, execute the following command:

```kubectl delete namespace camel-basic```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kubectl%20delete%20namespace%20camel-basic&completion=Removed%20the%20namespace%20from%20the%20cluster. "Cleans up the cluster after running the example"){.didact})
