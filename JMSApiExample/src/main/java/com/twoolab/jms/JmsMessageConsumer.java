package com.twoolab.jms;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * @author yeesheng on 12/08/2018
 * @project JMSApiExample
 */
public class JmsMessageConsumer implements Runnable {
    private final String hostname;
    private final String channel;
    private final int port;
    private final String queueManager;
    private final String readQueueName;
    private final boolean ssl;

    private QueueConnection connection;
    private QueueSession session;

    private volatile boolean running;

    private static final Logger logger = LoggerFactory.getLogger(JmsMessageConsumer.class);

    public JmsMessageConsumer(String hostname, String channel, int port,
                              String queueManager, String readQueueName, boolean ssl) {

        this.hostname = hostname;
        this.channel = channel;
        this.port = port;
        this.queueManager = queueManager;
        this.readQueueName = readQueueName;
        this.ssl = ssl;
    }

    private void initQueue() throws JMSException {
        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
        mqQueueConnectionFactory.setHostName(hostname);
        mqQueueConnectionFactory.setChannel(channel);
        mqQueueConnectionFactory.setPort(port);
        mqQueueConnectionFactory.setQueueManager(queueManager);
        mqQueueConnectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
        if (ssl) {
            mqQueueConnectionFactory.setSSLCipherSuite("TLS_RSA_WITH_AES_128_CBC_SHA256");
        }

        connection = mqQueueConnectionFactory.createQueueConnection();
        connection.start();

        session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    public void run() {
        logger.info("Thread[{}] - Message receiver", Thread.currentThread().getName());
        try {
            initQueue();
            running = true;
            while (running) {
                this.receiveMessage();
            }
        } catch (Exception e) {
            running = false;
            e.printStackTrace();
        } finally {
            try {
                session.close();
                connection.close();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void receiveMessage() throws JMSException {
        MessageConsumer consumer = session.createConsumer(session.createQueue(readQueueName));
        Message message = consumer.receive(1000);
        if (message instanceof TextMessage) {
            TextMessage receivedMsg = (TextMessage) message;
            try {
                logger.info(String.format("Message received: %s, Thread: %s%n",
                        receivedMsg.getText(),
                        Thread.currentThread().getName()));

                MessageProducer producer = session.createProducer(message.getJMSReplyTo());
//                producer.setTimeToLive(2 * 1000);
                producer.send(receivedMsg);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void destroy() throws JMSException {
        logger.info("Closing sessions...");
        running = false;
    }
}
