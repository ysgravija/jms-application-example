package com.twoolab.jms;

/**
 * @author yeesheng on 13/08/2018
 * @project JMSExample
 */
public class JmsProducerConfig {
    private String hostName;
    private String channel;
    private int port;
    private String queueManager;
    private String queue;
    private String replyQueue;
    private boolean ssl;

    public JmsProducerConfig(String hostName, String channel, int port, String queueManager, String queue, String replyQueue, boolean ssl) {
        this.hostName = hostName;
        this.channel = channel;
        this.port = port;
        this.queueManager = queueManager;
        this.queue = queue;
        this.replyQueue = replyQueue;
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

    public String getReplyQueue() {
        return replyQueue;
    }

    public void setReplyQueue(String replyQueue) {
        this.replyQueue = replyQueue;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

}
