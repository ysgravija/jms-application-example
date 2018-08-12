package com.twoolab;

import com.twoolab.jms.QueueReceiverRunnable;
import com.twoolab.jms.SyncMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;

/**
 * @author yeesheng on 11/08/2018
 * @project JMSApiExample
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String consumerHostname = "localhost";
    private static final int consumerPort = 1411;
    private static final String consumerChannel = "DEV.APP.SVRCONN";
    private static final String consumerQueueManager = "QM1";
    private static final String consumerQueue = "DEV.QUEUE.1";

    private static final String producerHostname = "localhost";
    private static final int producerPort = 1411;
    private static final String producerChannel = "DEV.APP.SVRCONN";
    private static final String producerQueueManager = "QM1";
    private static final String producerReplyQueue = "DEV.QUEUE.3";



    public static void main(String[] args) throws Exception {
        logger.info("Start application...");
        try {
            QueueReceiverRunnable consumerRunnable = new QueueReceiverRunnable(
                    consumerHostname, consumerChannel, consumerPort,
                    consumerQueueManager, consumerQueue, true);


            Thread consumerThread = new Thread(consumerRunnable);
            consumerThread.start();

            Thread producerThread = new Thread(getBulkSenderRunnable());
            producerThread.start();


            Thread.sleep(30 * 1000);
            // Stopping producer thread
            if (producerThread.isAlive()) {
                logger.info("Producer thread still alive.. destroying producer thread now..");
                producerThread.join();
            }

            // Stopping consumer thread
            consumerRunnable.destroy();
            consumerThread.join();

            logger.info("Stopping application!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Runnable getBulkSenderRunnable() {
        // Create sender runnable
        Runnable producerRunnable = () -> {
            try {
                SyncMessageSender sender = new SyncMessageSender(
                        producerHostname, producerChannel, producerPort,
                        producerQueueManager, consumerQueue, producerReplyQueue, true);

                for (int i = 0; i < 100; i++) {
                    String msg = "Sending test message " + i;
                    sender.send(msg, false);
                    Thread.sleep(100);
                }

                sender.destroy();
            } catch (InterruptedException | JMSException e) {
                e.printStackTrace();
            }
        };
        return producerRunnable;
    }
}
