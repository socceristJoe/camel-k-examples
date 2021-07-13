# Camel Azure Storage Blob Source Basic Example

This example demonstrates how to get started with Camel based Knative sources by showing you some of the most important
features that you should know before trying to develop more complex examples.

You can find more information about Apache Camel and Apache Camel K on the [official Camel website](https://camel.apache.org).

## Before you begin

Read the general instructions in the [root README.md file](../README.md) for setting up your environment and the Kubernetes cluster before looking at this example.

Make sure you've read the [installation instructions](https://camel.apache.org/camel-k/latest/installation/installation.html) for your specific
cluster before starting the example.

You should open this file with [Didact](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-didact) if available on your IDE.

## Requirements

**Kubectl CLI**

**Connection to a Kubernetes cluster**

**Apache Camel K CLI ("kamel")**

**Knative installed on the cluster**

The cluster also needs to have Knative installed and working. Refer to the [official Knative documentation](https://knative.dev/v0.15-docs/install/) for information on how to install it in your cluster.

**Knative Camel Source installed on the cluster**

The cluster also needs to have installed the Knative Camel Source from the camel.yaml in the [Eventing Sources release page](https://github.com/knative/eventing-contrib/releases/tag/v0.15.0)

### Optional Requirements

The following requirements are optional. They don't prevent the execution of the demo, but may make it easier to follow.

**VS Code Extension Pack for Apache Camel**

## 1. Preparing the namespace

## 2. Preparing the environment

This repository contains a simple [azure-storage-blob.properties] that contains the access key for accessing the Azure Storage Blob containers.

```
kubectl -n camel-api create secret generic azure-storage-blob --from-file=azure-storage-blob.properties
```

As the example levareges [Knative Eventing channels](https://knative.dev/v0.15-docs/eventing/channels/), we need to create the one that the example will use:

```
kubectl apply -f azure-storage-blob-channel.yaml
```

## 2. Running a Camel Source

This repository contains a simple Camel Source based on the [Azure Storage Blob Service component](https://camel.apache.org/components/latest/azure-storage-blob-component.html) that forward messages events received on the Azure Blob container orders to a Knative channel named `azure-blob`.

Use the following command to deploy the Camel Source:

```
kubectl apply -f azure-storage-blob-source.yaml
```

## 2. Running a basic integration to create Azure Storage Blob for consumption by the Camel Source

You need a producer adding data to `orders` Azure Blob Container to try this example. This integration
comes with a sample producer that will upload a xml file.

```
kamel run --secret azure-storage-blob azure-storage-blob-producer.groovy
```

If everything is ok, after the build phase finishes, you should see the Camel integration running.

## 3. Running a basic integration that split the order in line items and  forward them to `line-item-outbox` container
```
kamel run azure-storage-blob-consumer.groovy --dev --secret azure-storage-blob
```

If everything is ok, after the build phase finishes, you should see the Camel integration running.


## 4. Uninstall

To cleanup everything, execute the following command:

```kubectl delete namespace camel-k-azure-blob-knative```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kubectl%20delete%20namespace%20camel-k-azure-blob-knative&completion=Removed%20the%20namespace%20from%20the%20cluster. "Cleans up the cluster after running the example"){.didact})
