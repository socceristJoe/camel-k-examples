kamel install --base-image openjdk:15 --registry azrgeaipoc.azurecr.io --registry-auth-username AZRGEAIPOC --registry-auth-password $camelkacrsecret --build-timeout 1h --save -n solace


kamel run -n solace SolaceJms.java -d mvn:com.solacesystems:sol-jms:10.11.0  --dev




kamel run -n camel-basic SolacePahoProducer.java --property-file persistent.properties --dev

kamel run -n camel-basic SolacePahoConsumer.java --property-file application.properties --dev
kamel run -n camel-basic SolacePahoConsumer.java --property-file persistent.properties --dev



