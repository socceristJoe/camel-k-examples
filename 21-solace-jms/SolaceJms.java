import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Session;

public class SolaceJms  extends RouteBuilder {

        public void createQueue() throws Exception{
            SolConnectionFactory testFact = SolJmsUtility.createConnectionFactory();
            testFact.setHost("tcp://malta.corp.sensis.com:31253");
            testFact.setVPN("inder");
            testFact.setUsername("default");
            testFact.setPassword("");
            testFact.setDynamicDurables(true);
            Connection connection = testFact.createConnection();
            System.out.println("connection created");
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            System.out.println("session created");
            Queue queue = session.createQueue("createQueueTest");
            System.out.println("Queue created");
            session.close();
            System.out.println("session closed");
            connection.close();
            System.out.println("connection closed");
        }

        @Override
        public void configure() throws Exception {

            createQueue();
            ConnectionFactory testFact =
                    SolJmsUtility.createConnectionFactory("tcp://malta.corp.sensis.com:31253",
                            "default","","inder",null);

            CamelContext context = getContext();
            context.addComponent("test", JmsComponent.jmsComponentAutoAcknowledge(testFact));
            from("timer:ticker")
                    .setBody()
                    .constant("Hello from Camel-K")
                    .to(ExchangePattern.InOnly, "test:queue:createQueueTest")
                    .log("Messsage sent to target 1 :: ${body}");
        }
    }
