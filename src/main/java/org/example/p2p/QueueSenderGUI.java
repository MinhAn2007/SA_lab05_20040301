package org.example.p2p;

import org.apache.log4j.BasicConfigurator;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

public class QueueSenderGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private ConnectionFactory factory;
    private Destination destination;
    private JTextField textField;

    public QueueSenderGUI() {
        setTitle("Message Sender");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 150);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("Enter Message:");
        textField = new JTextField(20);
        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        panel.add(label, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);

        initializeJMS();
    }

    private void initializeJMS() {
        try {
            BasicConfigurator.configure();

            Properties settings = new Properties();
            settings.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

            Context ctx = new InitialContext(settings);

            factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
            destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        try (Connection connection = factory.createConnection("admin", "admin")) {
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);

            TextMessage message = session.createTextMessage(textField.getText());
            producer.send(message);

            JOptionPane.showMessageDialog(this, "Message sent successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new QueueSenderGUI();
            }
        });
    }
}
