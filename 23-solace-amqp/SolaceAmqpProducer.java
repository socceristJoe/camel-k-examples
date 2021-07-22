import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.amqp.AMQPConnectionDetails;
import org.apache.camel.language.bean.Bean;

public class SolaceAmqpProducer extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("timer:ticker")
                .setBody().constant("Hello from Camel-K")
                .to(ExchangePattern.InOnly, "solace-amqp:topic:joepoc").log("Messsage sent to target 1 :: ${body}");
    }

    @Bean(ref = "solace-amqp")
    AMQPConnectionDetails securedAmqpConnection() {
        return new AMQPConnectionDetails("amqps://mr-e3zjti4y6ai.messaging.solace.cloud:5671", "solace-cloud-client", "dnuqffh8dh8dgt4flludfrot26");
    }
}
