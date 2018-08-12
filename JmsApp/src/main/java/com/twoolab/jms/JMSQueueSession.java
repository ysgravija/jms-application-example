package com.twoolab.jms;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * @author yeesheng on 11/08/2018
 * @project JmsApp
 */
public class JMSQueueSession {

    private static final Logger logger = LoggerFactory.getLogger(JMSQueueSession.class);

    private QueueConnection queueConnection;
    private QueueSession queueSession;
    private String replyQueueName;

    public JMSQueueSession(String hostname, String channel, int port,
                           String queueManager, String replyQueueName) throws JMSException {

        this.replyQueueName = replyQueueName;

        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
        mqQueueConnectionFactory.setHostName(hostname);
        mqQueueConnectionFactory.setChannel(channel);
        mqQueueConnectionFactory.setPort(port);
        mqQueueConnectionFactory.setQueueManager(queueManager);
        mqQueueConnectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

        queueConnection = mqQueueConnectionFactory.createQueueConnection();
        queueConnection.start();
        queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public void sendMesg(String message, String requestQueueName, boolean readBack) {
        try {
            TextMessage textMessage = queueSession.createTextMessage(message);
            textMessage.setJMSReplyTo(queueSession.createQueue(replyQueueName));
            textMessage.setJMSType("mcd://xmlns");
            textMessage.setJMSExpiration(2 * 1000);
            textMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);

            QueueSender queueSender = queueSession.createSender(queueSession.createQueue(requestQueueName));
            queueSender.setTimeToLive(2 * 1000);
            queueSender.send(textMessage);
            queueSender.close();

            logger.info("Sent message with JMS Correlation ID: " + textMessage.getJMSMessageID());

            if (readBack) {
                String jmsCorrelationID = " JMSCorrelationID = '" + textMessage.getJMSMessageID() + "'";
                QueueReceiver queueReceiver = queueSession.createReceiver(queueSession.createQueue(replyQueueName), jmsCorrelationID);
                Message jmsMessage = queueReceiver.receive(60 * 1000);
                String responseMsg = ((TextMessage) jmsMessage).getText();
                queueReceiver.close();
                logger.info("ResponseMsg: \n" + responseMsg);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void close() throws JMSException {
        queueSession.close();
        queueConnection.close();
    }

}
