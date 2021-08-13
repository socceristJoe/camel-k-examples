import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.qpid.jms.JmsConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Session;

public class SolaceAmqpProducer extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        ConnectionFactory connectionFactory = new JmsConnectionFactory("solace-cloud-client",
                "dnuqffh8dh8dgt4flludfrot26", "amqps://mr-e3zjti4y6ai.messaging.solace.cloud:5671");
        
        CamelContext context = getContext();
        context.addComponent("solace-amqp", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        from("timer:ticker?period=2000&repeatCount=10").setBody().constant("Hello from Camel-K!!")
                .to(ExchangePattern.InOnly, "solace-amqp:topic:joepoc").log("Message sent to target 1 :: ${body}");
                // .to(ExchangePattern.InOnly, "solace-amqp:queue:joequeue").log("Message sent to target 1 :: ${body}");

    }
}
