package com.twoolab.jms;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * @author yeesheng on 12/08/2018
 * @project JMSApiExample
 */
public class QueueReceiverRunnable implements Runnable {
    private String hostname;
    private String channel;
    private int port;
    private String queueManager;
    private String readQueueName;

    private QueueConnection connection;
    private QueueSession session;
    private volatile boolean running;

    private static final Logger logger = LoggerFactory.getLogger(QueueReceiverRunnable.class);

    public QueueReceiverRunnable(String hostname, String channel, int port,
                                 String queueManager, String readQueueName) {

        this.hostname = hostname;
        this.channel = channel;
        this.port = port;
        this.queueManager = queueManager;
        this.readQueueName = readQueueName;
    }

    @Override
    public void run() {
        logger.info("Thread[{}] - Message receiver", Thread.currentThread().getName());
        try {
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
        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
        mqQueueConnectionFactory.setHostName(hostname);
        mqQueueConnectionFactory.setChannel(channel);
        mqQueueConnectionFactory.setPort(port);
        mqQueueConnectionFactory.setQueueManager(queueManager);
        mqQueueConnectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

        connection = mqQueueConnectionFactory.createQueueConnection();
        connection.start();

        session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(session.createQueue(readQueueName));

        Message message = consumer.receive(100);
        if (message instanceof TextMessage) {
            TextMessage receivedMsg = (TextMessage) message;
            try {
                logger.info(String.format("Message received: %s, Thread: %s%n",
                        receivedMsg.getText(),
                        Thread.currentThread().getName()));
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
