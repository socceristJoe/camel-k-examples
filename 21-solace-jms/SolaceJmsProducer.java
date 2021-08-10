import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;

import java.lang.System.Logger;

import javax.jms.Connection;
import javax.jms.Queue;
import javax.jms.Session;

public class SolaceJmsProducer extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost("tcps://mr-e3zjti4y6ai.messaging.solace.cloud:55443");
        connectionFactory.setUsername("solace-cloud-client");
        connectionFactory.setPassword("dnuqffh8dh8dgt4flludfrot26");
        connectionFactory.setVPN("joepoc");

        Connection connection = connectionFactory.createConnection(); 
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        CamelContext context = getContext();
        context.addComponent("solace-jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        from("timer:ticker?period=2000&repeatCount=10").setBody().constant("Hello from Camel-K!!")
                // .to(ExchangePattern.InOnly, "solace:queue:joe-jms-queue").log("Messsage sent to Solace Queue:: ${body}");
                .to(ExchangePattern.InOnly, "solace-jms:topic:joepoc").log("Message sent to Solace Topic:: ${body}");

        session.close();
        connection.close();
    }

    private void solaceroute() throws Exception {

    }
}
