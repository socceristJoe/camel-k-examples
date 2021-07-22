import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class SolaceRestProducer extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("timer:ticker?period=2000&repeatCount=10").setBody().constant("Hello from Camel-K!!")
                .setHeader(Exchange.CONTENT_TYPE, constant("text"))
                .setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http.HttpMethods.POST))
                .setHeader(Exchange.HTTP_QUERY, constant("Solace-delivery-mode=direct"))
                .setHeader(Exchange.HTTP_QUERY, constant("user=solace-cloud-client:dnuqffh8dh8dgt4flludfrot26"))
                .to("http://mr-e3zjti4y6ai.messaging.solace.cloud:9443/joepoc")
                .log("Messsage sent to Solace Topic:: ${body}");

    }

}
