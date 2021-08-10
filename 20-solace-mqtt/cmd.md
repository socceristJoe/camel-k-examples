kamel run SolacePahoMqtt5.java -n camel-basic --dev

kamel run -n camel-basic SolacePahoProducer.java --property-file application.properties --dev
kamel run -n camel-basic SolacePahoProducer.java --property-file persistent.properties --dev

kamel run -n solace SolacePahoConsumer.java --property-file application.properties --dev
kamel run -n camel-basic SolacePahoConsumer.java --property-file persistent.properties --dev



