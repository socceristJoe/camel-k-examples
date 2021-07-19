import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;

import javax.jms.Connection;
import javax.jms.Queue;
import javax.jms.Session;

public class SolaceJms extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost("tcps://mr-e3zjti4y6ai.messaging.solace.cloud:55443");
        connectionFactory.setUsername("solace-cloud-client");
        connectionFactory.setPassword("dnuqffh8dh8dgt4flludfrot26");
        connectionFactory.setVPN("joepoc");

        Connection connection = connectionFactory.createConnection();
        System.out.println("connection created");

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        System.out.println("session created");

        Queue queue = session.createQueue("joequeue");
        System.out.println("Queue created");

        CamelContext context = getContext();
        context.addComponent("solace", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        from("timer:ticker").setBody().constant("Hello from Camel-K")
                .to(ExchangePattern.InOnly, "solace:queue:joequeue").log("Messsage sent to target 1 :: ${body}");
    }
}
