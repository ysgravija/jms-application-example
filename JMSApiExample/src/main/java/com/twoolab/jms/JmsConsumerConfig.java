package com.twoolab.jms;

/**
 * @author yeesheng on 13/08/2018
 * @project JMSExample
 */
public class JmsConsumerConfig {
    private String hostName;
    private String channel;
    private int port;
    private String queueManager;
    private String queue;
    private boolean ssl;

    public JmsConsumerConfig(String hostName, String channel, int port, String queueManager, String queue, boolean ssl) {
        this.hostName = hostName;
        this.channel = channel;
        this.port = port;
        this.queueManager = queueManager;
        this.queue = queue;
        this.ssl = ssl;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
}
