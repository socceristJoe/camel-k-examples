import org.apache.camel.builder.RouteBuilder;

public class ActiveMqSender extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        from("timer:point2point-timer?period=2000&repeatCount=10")
                .transform().constant("Hi this is Joe")
                .log("${body}")
                .multicast()
                .to("activemq:joepoc-camel-activemq-helloJoe").log("File sent to Queue:joepoc-camel-activemq-helloJoe")
                .to("activemq:joepoc-camel-activemq-helloRyan").log("File sent to Queue:joepoc-camel-activemq-helloRyan")
                .end();
    }
}
