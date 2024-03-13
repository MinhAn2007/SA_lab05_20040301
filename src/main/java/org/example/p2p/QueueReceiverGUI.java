package org.example.p2p;

import org.apache.log4j.BasicConfigurator;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class QueueReceiverGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private ConnectionFactory factory;
    private Destination destination;

    public QueueReceiverGUI() {
        setTitle("Message Receiver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(messageArea);

        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);

        initializeJMS(messageArea);
    }

    private void initializeJMS(JTextArea messageArea) {
        try {
            BasicConfigurator.configure();

            Properties settings = new Properties();
            settings.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

            Context ctx = new InitialContext(settings);

            factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
            destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");

            Connection connection = factory.createConnection("admin", "admin");
            connection.start();

            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(destination);

            // Nhận tin nhắn chỉ một lần
            Message message = consumer.receive();
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                messageArea.append("Received: " + text + "\n");
            } else if (message instanceof ObjectMessage) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                System.out.println(objectMessage);
            }
            session.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new QueueReceiverGUI();
            }
        });
    }
}
