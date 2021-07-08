// camel-k: language=java dependency=camel-quarkus-openapi-java 

import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;

public class API extends RouteBuilder {
  @Override
  public void configure() throws Exception {

    // All endpoints starting from "direct:..." reference an operationId defined
    // in the "openapi.yaml" file.

    // List the object names available in the S3 bucket
    from("direct:list")
      .to("azure-storage-blob://{{accountName}}/{{containerName}}?accessKey={{accessKey}}&operation=listBlobs")
      .split(simple("${body}"), AggregationStrategies.groupedBody())
        .transform().simple("${body.key}")
      .end()
      .marshal().json();


    // Get an object from the S3 bucket
    from("direct:get")
      .setHeader("CamelAwsS3Key", simple("${header.name}"))
      .to("azure-storage-blob://{{accountName}}/{{containerName}}?accessKey={{accessKey}}&operation=getBlob")
      .convertBodyTo(String.class);


    // Upload a new object into the S3 bucket
    from("direct:create")
      .setHeader("CamelAwsS3Key", simple("${header.name}"))
      .to("azure-storage-blob://{{accountName}}/{{containerName}}?accessKey={{accessKey}}&operation=uploadBlockBlob")
      .setBody().constant("");


    // Delete an object from the S3 bucket
    from("direct:delete")
      .setHeader("CamelAwsS3Key", simple("${header.name}"))
      .to("azure-storage-blob://{{accountName}}/{{containerName}}?accessKey={{accessKey}}&operation=deleteBlob")
      .setBody().constant("");

  }
}