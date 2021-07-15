import org.apache.camel.builder.RouteBuilder;

public class SolacePahoProducer extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:point2point-timer?period=2000&repeatCount=10")
                .transform().constant("Hi this is Joe!!")
                .log("${body}")
                .multicast()
                .to("paho:joepoc")
                .log("File sent to Solace Topic:joepoc")
                .end();
    }
}
