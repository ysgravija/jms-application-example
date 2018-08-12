package com.twoolab.jms;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

/**
 * @author yeesheng on 11/08/2018
 * @project JMSApiExample
 */
public class AsyncMessageReceiver implements MessageListener {
    private String hostname;
    private String channel;
    private int port;
    private String queueManager;
    private String readQueueName;

    private QueueConnection connection;
    private QueueSession session;

    private static final Logger logger = LoggerFactory.getLogger(AsyncMessageReceiver.class);

    public AsyncMessageReceiver(String hostname, String channel, int port,
                                String queueManager, String readQueueName) throws JMSException {

        this.hostname = hostname;
        this.channel = channel;
        this.port = port;
        this.queueManager = queueManager;
        this.readQueueName = readQueueName;
    }

    public void startListener() throws JMSException {
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
        consumer.setMessageListener(this);
    }

    @Override
    public void onMessage(Message message) {
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
        session.close();
        connection.close();
    }
}
