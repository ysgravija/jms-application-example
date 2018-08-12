package com.twoolab.jms;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author yeesheng on 10/08/2018
 * @project z
 */
public class ConsumerProcessor implements Processor {
    private Logger logger = LoggerFactory.getLogger(ConsumerProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        String request = exchange.getIn().getBody(String.class);
        logger.info("Body: " + request);

        Date now = new Date();
        String response = now.toString() + "- <Response>Received message[" + request.substring(request.indexOf("index=")) + "]</Response>";
        exchange.getIn().setBody(response);
    }
}
