import org.apache.camel.builder.RouteBuilder;

public class ActiveMqSender extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        from("timer:point2point-timer?period=2000&repeatCount=10")
                .transform().constant("Hi this is Joe")
                .log("${body}")
                .multicast()
                .to("activemq:joepoc-camel-activemq-pubsub01").log("File sent to Queue:joepoc-camel-activemq-pubsub01")
                .to("activemq:joepoc-camel-activemq-pubsub02").log("File sent to Queue:joepoc-camel-activemq-pubsub02")
                .to("activemq:joepoc-camel-activemq-pubsub03").log("File sent to Queue:joepoc-camel-activemq-pubsub03")
                .end();
    }
}
