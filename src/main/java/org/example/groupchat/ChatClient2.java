package org.example.groupchat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient2 extends JFrame {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;
    private PrintWriter output;

    public ChatClient2() {
        setTitle("Chat Client2");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(messagePanel, BorderLayout.SOUTH);

        try {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            output = new PrintWriter(socket.getOutputStream(), true);
            new Thread(new ServerListener(socket, chatArea)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(messageField.getText());
                messageField.setText("");
            }
        });

        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(messageField.getText());
                messageField.setText("");
            }
        });
    }

    private void sendMessage(String message) {
        output.println(message);
    }

    private class ServerListener implements Runnable {
        private Socket socket;
        private JTextArea chatArea;

        public ServerListener(Socket socket, JTextArea chatArea) {
            this.socket = socket;
            this.chatArea = chatArea;
        }

        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                while ((message = input.readLine()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClient2().setVisible(true));
    }
}
