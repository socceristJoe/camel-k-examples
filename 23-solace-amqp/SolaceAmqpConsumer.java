import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.amqp.AMQPComponent;

public class SolaceAmqpConsumer extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        CamelContext context = getContext();
        context.addComponent("solace-amqp", AMQPComponent.amqpComponent("amqps://mr-e3zjti4y6ai.messaging.solace.cloud:5671","solace-cloud-client", "dnuqffh8dh8dgt4flludfrot26"));

        from("solace-amqp:topic:joepoc").log("Message received from target 1 :: ${body}")
                .to("log:amqp-consumer");
    }
}
