package com.twoolab;

import com.twoolab.jms.JmsConsumerConfig;
import com.twoolab.jms.JmsMessageConsumer;
import com.twoolab.jms.JmsProducerConfig;
import com.twoolab.jms.JmsMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yeesheng on 11/08/2018
 * @project JMSApiExample
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static List<JmsMessageConsumer> messageConsumers = new ArrayList<>();
    private static List<Thread> threads = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        logger.info("Start application...");
        try {
            // Consumers
            initMessageConsumer();

            // Producers
            initMessageProducer();

            Thread.sleep(30 * 1000);

            stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Stopping application!!");
    }

    private static void initMessageConsumer() throws JMSException, InterruptedException {
        messageConsumers.add(createReceiverRunnable(
                new JmsConsumerConfig("localhost", "DEV.APP.SVRCONN", 1411,
                        "QM1", "DEV.QUEUE.1", false))
        );

        messageConsumers.add(createReceiverRunnable(
                new JmsConsumerConfig("localhost", "DEV.APP.SVRCONN", 1412,
                "QM2", "DEV.QUEUE.1", false))
        );

        for (JmsMessageConsumer r : messageConsumers) {
            Thread thread = new Thread(r);
            thread.start();
            threads.add(thread);
        }
    }

    private static void initMessageProducer() throws JMSException, InterruptedException {
        Thread producer1 = new Thread(
                getBulkSenderRunnable(new JmsProducerConfig("localhost", "DEV.APP.SVRCONN", 1411,
                "QM1", "DEV.QUEUE.1", "DEV.QUEUE.3", false))
        );

        Thread producer2 = new Thread(getBulkSenderRunnable(
                new JmsProducerConfig("localhost", "DEV.APP.SVRCONN", 1412,
                        "QM2", "DEV.QUEUE.1", "DEV.QUEUE.3", false)
        ));

        producer1.start();
        producer2.start();

        threads.add(producer1);
        threads.add(producer2);
    }

    private static void stop() throws JMSException, InterruptedException {
        // Stopping threads
        for (JmsMessageConsumer r : messageConsumers) {
            r.destroy();
        }

        for (Thread t : threads) {
            if (t.isAlive()) {
                t.join();
            }
        }
    }

    private static JmsMessageConsumer createReceiverRunnable(JmsConsumerConfig config) {
       return new JmsMessageConsumer(
               config.getHostName(), config.getChannel(), config.getPort(),
               config.getQueueManager(), config.getQueue(), config.isSsl()
       );
    }

    private static Runnable getBulkSenderRunnable(JmsProducerConfig config) {
        // Create sender runnable
        Runnable producerRunnable = () -> {
            try {
                JmsMessageProducer sender = new JmsMessageProducer(
                        config.getHostName(), config.getChannel(), config.getPort(),
                        config.getQueueManager(), config.getQueue(), config.getReplyQueue(), config.isSsl());

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
