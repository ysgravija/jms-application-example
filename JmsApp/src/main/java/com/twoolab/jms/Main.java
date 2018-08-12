package com.twoolab.jms;

import javax.jms.JMSException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author yeesheng on 10/08/2018
 * @project JmsApp
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Runnable runnable = () -> {
            try {
                sendBulkMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        thread.start();
        thread2.start();
    }

    private static void sendBulkMessage() throws JMSException, InterruptedException, NoSuchAlgorithmException {
        try {
            JMSQueueSession session = new JMSQueueSession(
                    "localhost", "DEV.APP.SVRCONN", 1411, "QM1", "DEV.QUEUE.3");

            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            for (int i = 0; i < 10000; i++) {
                session.sendMesg(
                        "Test message index=" + secureRandom.nextInt(100000),
                        "DEV.QUEUE.1",
                        false);

                Thread.sleep(100);
            }
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}





//        try {
//            /*MQ Configuration*/
//            MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
//            mqQueueConnectionFactory.setHostName("localhost");
//            mqQueueConnectionFactory.setChannel("DEV.APP.SVRCONN");//communications link
//            mqQueueConnectionFactory.setPort(1414);
//            mqQueueConnectionFactory.setQueueManager("QM1");//service provider
//            mqQueueConnectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
//
//            /*Create Connection */
//            QueueConnection queueConnection = mqQueueConnectionFactory.createQueueConnection();
//            queueConnection.start();
//
//            /*Create session */
//            QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
//
//            /*Create response queue */
//            Queue queue = queueSession.createQueue("DEV.QUEUE.3");
//
//            /*Create text message */
//            TextMessage textMessage = queueSession.createTextMessage("put some message here");
//            textMessage.setJMSReplyTo(queue);
////            textMessage.setJMSType("mcd://xmlns");//message type
//            textMessage.setJMSExpiration(2*1000);//message expiration
//            textMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT); //message delivery mode either persistent or non-persistemnt
//
//            /*Create sender queue */
//            QueueSender queueSender = queueSession.createSender(queueSession.createQueue("DEV.QUEUE.1"));
//            queueSender.setTimeToLive(2*1000);
//            queueSender.send(textMessage);
//
//            /*After sending a message we get message id */
//            System.out.println("after sending a message we get message id "+ textMessage.getJMSMessageID());
//            String jmsCorrelationID = " JMSCorrelationID = '" + textMessage.getJMSMessageID() + "'";
//
//
//            /*Within the session we have to create queue reciver */
//            QueueReceiver queueReceiver = queueSession.createReceiver(queue,jmsCorrelationID);
//
//
//            /*Receive the message from*/
//            Message message = queueReceiver.receive(60*1000);
//            String responseMsg = ((TextMessage) message).getText();
//            System.out.println("ResponseMsg: \n" + responseMsg);
//
//            queueSender.close();
//            queueReceiver.close();
//            queueSession.close();
//            queueConnection.close();
//
//
//        } catch (JMSException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
