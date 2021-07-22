kamel run SolacePahoMqtt5.java -n camel-basic --dev

kamel run -n camel-basic SolacePahoProducer.java --property-file application.properties --dev
kamel run -n camel-basic SolacePahoProducer.java --property-file persistent.properties --dev

kamel run -n camel-basic SolacePahoConsumer.java --property-file application.properties --dev
kamel run -n camel-basic SolacePahoConsumer.java --property-file persistent.properties --dev



https://cwiki.apache.org/confluence/display/CAMEL/AMQP
https://examples.javacodegeeks.com/enterprise-java/apache-camel/apache-camel-amqp-example/
https://github.com/welshstew/camel-amqp-example/blob/master/src/main/java/com/nullendpoint/Application.java



