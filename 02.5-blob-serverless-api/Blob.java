import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.AggregationStrategies;

public class Blob extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:java?period=1000&repeatCount=2")
                .to("azure-storage-blob://{{accountName}}/{{containerName}}?accessKey={{accessKey}}&operation=listBlobs")
                .split(simple("${body}"), AggregationStrategies.groupedBody()).transform().simple("${body.key}").end()
                .marshal().json();
    }
}
