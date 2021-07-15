import org.apache.camel.builder.RouteBuilder;

public class SolacePahoConsumer extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("paho:joepoc").to("log:mqtt-consumer");
    }
}
