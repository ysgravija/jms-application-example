package com.twoolab.jms;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * @author yeesheng on 11/08/2018
 * @project JmsApp
 */
public class SyncMessageSender {

    private static final Logger logger = LoggerFactory.getLogger(SyncMessageSender.class);

    private final QueueConnection connection;
    private final QueueSession session;
    private final MessageProducer producer;
    private final String replyQueueName;

    public SyncMessageSender(String hostname, String channel, int port,
                             String queueManager, String sendQueueName, String replyQueueName, boolean ssl) throws JMSException {

        this.replyQueueName = replyQueueName;

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
        this.producer = session.createProducer(session.createQueue(sendQueueName));
    }

    public void send(String message, boolean readBack) {
        try {
            TextMessage textMessage = session.createTextMessage(message);
            textMessage.setJMSReplyTo(session.createQueue(replyQueueName));
            textMessage.setJMSType("mcd://xmlns");
            textMessage.setJMSExpiration(2 * 1000);
            textMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);

//            producer.setTimeToLive(2 * 1000);
            producer.send(textMessage);

            logger.info("Sent message with JMS Correlation ID: " + textMessage.getJMSMessageID());

            if (readBack) {
                String jmsCorrelationID = " JMSCorrelationID = '" + textMessage.getJMSMessageID() + "'";
                QueueReceiver queueReceiver = session.createReceiver(session.createQueue(replyQueueName), jmsCorrelationID);
                Message jmsMessage = queueReceiver.receive(60 * 1000);
                String responseMsg = ((TextMessage) jmsMessage).getText();
                queueReceiver.close();
                logger.info("ResponseMsg: \n" + responseMsg);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void destroy() throws JMSException {
        session.close();
        connection.close();
    }

}
