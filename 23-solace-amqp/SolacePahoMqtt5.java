import org.apache.camel.builder.RouteBuilder;

public class SolacePahoMqtt5 extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:point2point-timer?period=2000&repeatCount=10")
                .transform().constant("Hi this is Joe")
                .log("${body}")
                .multicast()
                .to("paho:joepoc/joepocmqtt?serverURIs=ssl://mr-e3zjti4y6ai.messaging.solace.cloud:8883?username=solace-cloud-client?password=dnuqffh8dh8dgt4flludfrot26")
                .log("File sent to Queue:joepoc-camel-activemq-pubsub01")
                .end();
    }
}
