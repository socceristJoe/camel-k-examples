# Camel K Serverless API Example

This example demonstrates how to write an API based Camel K integration, from the design of the OpenAPI definition 
to the implementation of the specific endpoints up to the deployment as serverless API in Knative.

In this specific example, the API enables users to store generic objects, such as files, in a backend system, allowing all CRUD operation on them.

The backend is an Amazon AWS S3 bucket that you might provide. In alternative, you'll be given instructions on how to 
create a simple [Minio](https://min.io/) backend, which uses a S3 compatible protocol.

## Before you begin

Read the general instructions in the [root README.md file](../README.md) for setting up your environment and the Kubernetes cluster before looking at this example.

Make sure you've read the [installation instructions](https://camel.apache.org/camel-k/latest/installation/installation.html) for your specific 
cluster before starting the example.
## Requirements

**Kubectl CLI**

**Connection to a Kubernetes cluster**

**Apache Camel K CLI ("kamel")**

**Istio**
https://istio.io/latest/docs/setup/getting-started/
```
curl -L https://istio.io/downloadIstio | ISTIO_VERSION=1.9.6 TARGET_ARCH=x86_64 sh -
```

**Knative installed on the cluster**

The cluster also needs to have Knative installed and working. Refer to the [official Knative documentation](https://knative.dev/v0.15-docs/install/) for information on how to install it in your cluster.

https://knative.dev/docs/install/install-serving-with-yaml/
https://github.com/knative/serving/issues/8323
```
kubectl apply -f https://github.com/knative/serving/releases/download/v0.23.0/serving-crds.yaml
kubectl apply -f https://github.com/knative/serving/releases/download/v0.23.0/serving-core.yaml
kubectl apply -f https://github.com/knative/net-istio/releases/download/v0.23.0/istio.yaml
kubectl create cm in net-istio.yaml
kubectl apply -f https://github.com/knative/net-istio/releases/download/v0.23.0/net-istio.yaml
#remove admision controller
kubectl delete ValidatingWebhookConfiguration  -l serving.knative.dev/release=v0.23.0
kubectl delete MutatingWebhookConfiguration  -l serving.knative.dev/release=v0.23.0
#real dns
# Here knative.example.com is the domain suffix for your cluster
*.knative.joepoc.com == A 13.67.34.216

kubectl patch configmap/config-domain \
  --namespace knative-serving \
  --type merge \
  --patch '{"data":{"knative.joepoc.com":""}}'
```
https://knative.dev/docs/install/install-eventing-with-yaml/
```
kubectl apply -f https://github.com/knative/eventing/releases/download/v0.23.0/eventing-crds.yaml
kubectl apply -f https://github.com/knative/eventing/releases/download/v0.23.0/eventing-core.yaml
```
https://knative.dev/docs/install/install-extensions/
```
kubectl apply -f https://github.com/knative-sandbox/eventing-camel/releases/download/v0.23.0/camel.yaml
```

### Optional Requirements

The following requirements are optional. They don't prevent the execution of the demo, but may make it easier to follow.

**VS Code Extension Pack for Apache Camel**

## 1. Preparing the namespace

Let's open a terminal and go to the example directory:

```
cd 02-serverless-api
```
We're going to create a namespace named `camel-api` for running the example. To create it, execute the following command:

```
kubectl create namespace camel-api
```
Now we can set the `camel-api` namespace as default namespace for the following commands:

```
kubectl config set-context --current --namespace=camel-api
```

https://portal.azure.com/#@pgone.onmicrosoft.com/resource/subscriptions/66f32aee-5b1b-4577-834c-0aee44c29aac/resourceGroups/AZ-RG-EAI-Sandbox/providers/Microsoft.ContainerRegistry/registries/AZRGEAIPOC/accessKey

```
kamel install --base-image openjdk:11 --registry azrgeaipoc.azurecr.io --registry-auth-username AZRGEAIPOC --registry-auth-password $camelkacrsecret --build-timeout 1h --save -n camel-api
```

NOTE: The `kamel install` command requires some prerequisites to be successful in some situations, e.g. you need to enable the registry addon on Minikube. Refer to the [Camel K install guide](https://camel.apache.org/camel-k/latest/installation/installation.html) for cluster-specific instructions.

To check that Camel K is installed we'll retrieve the IntegrationPlatform object from the namespace:

```
kubectl get integrationplatform
```
You should find an IntegrationPlatform in status `Ready`.

You can now proceed to the next section.

## 2. Configuring the object storage backend

You have two alternative options for setting up the S3 backend that will be used to store the objects via the Camel K API: 
you can use an existing S3 bucket of your own or you can set up a local S3 compatible object storage.

### 2.1 [Alternative 1] I don't have a S3 bucket: let's install a Minio backend

create PV
https://portal.azure.com/#blade/Microsoft_Azure_FileStorage/FileShareMenuBlade/overview/storageAccountId/%2Fsubscriptions%2F66f32aee-5b1b-4577-834c-0aee44c29aac%2FresourceGroups%2FAZ-RG-EAI-Sandbox%2Fproviders%2FMicrosoft.Storage%2FstorageAccounts%2Fstorageaccountazrge8711/path/minio-camel-api/protocol/SMB
```
kubectl -n camel-api create secret generic joe-secret --from-literal=azurestorageaccountname=storageaccountazrge8711 --from-literal=azurestorageaccountkey=

```

The `test` directory contains an all-in-one configuration file for creating a Minio backend that will provide a S3 compatible protocol
for storing the objects.

Open the ([test/minio.yaml] file to check its content before applying.

To create the minio backend, just apply the provided file:

```
kubectl apply -f test/minio.yaml
```

That's enough to have a test object storage to use with the API integration.

### 2.1 [Alternative 2] I have a S3 bucket

If you have a S3 bucket and you want to use it instead of the test backend, you can do it. The only 
things that you need to provide are a **AWS Access Key ID and Secret** that you can obtain from the Amazon AWS console.

Edit the to set the right value for the properties `camel.component.aws-s3.access-key` and `camel.component.aws-s3.secret-key`.
Those properties will be automatically injected into the Camel `aw3-s3` component.

## 3. Designing the API

An object store REST API is provided in the [openapi.yaml] file.

It contains operations for:
- Listing the name of the contained objects
- Creating a new object
- Getting the content of an object
- Deleting an object

The file can be edited manually or better using an online editor, such as [Apicurio](https://studio.apicur.io/).

## 4. Running the API integration

The endpoints defined in the API can be implemented in a Camel K integration using a `direct:<operationId>` endpoint.
This has been implemented in the [API.java] file.

To run the integration, you need to link it to the proper configuration, that depends on what configuration you've chosen.

### 4.1 [Alternative 1] Using the test Minio server

As alternative, to connect the integration to the **test Minio server** deployed before using the [test/MinioCustomizer.java] class:

```
kamel -n camel-api run API.java --source test/MinioCustomizer.java --property file:test/minio.properties --dev
```

### 4.2 [Alternative 2] Using the S3 service

To connect the integration to the **AWS S3 service**:

```
kamel -n camel-api --trait knative-service.enabled=true run API.java --property-file blob.properties --dev
```


## 5. Using the API

After running the integration API, you should be able to call the API endpoints to check its behavior.

Make sure the integration is running, by checking its status:

```
camel-api get integrations
```

An integration named `api` should be present in the list and it should be in status `Running`. There's also a `kamel get` command which is an alternative way to list all running integrations.

NOTE: it may take some time, the first time you run the integration, for it to reach the `Running` state.

After the integraiton has reached the running state, you can get the route corresponding to it via the following command:

```
URL=$(kubectl get routes.serving.knative.dev api -o jsonpath='{.status.url}')
URL=$(camel-api get routes.serving.knative.dev api -o jsonpath='{.status.url}')

```

You can print the route to check if it's correct:

```
echo $URL
```

NOTE: ensure that you've followed all the instructions in the Knative documentation during installation, especially the DNS part is fundamental for being able to contact the API.

You can now play with it! What follows is a list of commands that you can run to use the API and check if it's working.

Get the list of objects:
```
curl -i $URL/
curl -H "Host: api.camel-api.example.com" http://13.67.34.216:32169
```

Looking at the pods, you should find a pod corresponding to the API integration:

```
kubectl get pods
```

If you wait **at least one minute** without invoking the API, you'll find that the pod will disappear.
Calling the API again will make the pod appear to serve the request. This is done to save resources and it's one the main features of Knative Serving.

You can continue with other commands.

Upload an object:
```
curl -i -H "Host: api.camel-api.example.com" -X PUT --header "Content-Type: application/octet-stream" --data-binary "@vip.yaml" http://13.67.34.216:32169/example
```

Get the new list of objects:
```
curl -i $URL/
```

Get the content of a file:
```
curl -i $URL/example
```

Delete the file:
```
curl -i -X DELETE $URL/example
```

Get (again) the new list of objects:
```
curl -i $URL/
```


## 6. Uninstall
```
kamel -n camel-api delete api
```

To cleanup everything, execute the following command:

```kubectl delete namespace camel-api```

kamel uninstall -n camel-api
