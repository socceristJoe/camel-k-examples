import org.apache.camel.builder.RouteBuilder;

public class SolacePaho extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:point2point-timer?period=2000&repeatCount=10")
                .transform().constant("Hi this is Qiao")
                .log("${body}")
                .multicast()
                .to("paho:joepoc/joepocmqtt")
                .log("File sent to Queue:joepoc-camel-activemq-pubsub01")
                .end();
    }
}
