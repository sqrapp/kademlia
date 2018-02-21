package com.soriole.dht.kademlia.message;

import com.soriole.dht.kademlia.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Responds to a ContentLookupMessage by sending a ContentMessage containing the requested content;
 * if the requested content is not found, a NodeReplyMessage containing the K closest nodes to the request key is sent.
 *
 * @author Joshua Kissoon
 * @since 20140226
 */
public class ContentLookupReceiver implements Receiver {
    private static final Logger logger = LoggerFactory.getLogger(ContentLookupReceiver.class);

    private final KadServer server;
    private final KademliaNode localNode;
    private final KademliaDHT dht;
    private final KadConfiguration config;

    public ContentLookupReceiver(KadServer server, KademliaNode localNode, KademliaDHT dht, KadConfiguration config) {
        this.server = server;
        this.localNode = localNode;
        this.dht = dht;
        this.config = config;
    }

    @Override
    public void receive(Message incoming, int comm) throws IOException {
        ContentLookupMessage msg = (ContentLookupMessage) incoming;
        this.localNode.getRoutingTable().insert(msg.getOrigin());

        /* Check if we can have this data */
        if (this.dht.contains(msg.getParameters())) {
            try {
                /* Return a ContentMessage with the required data */
                ContentMessage cMsg = new ContentMessage(localNode.getLocalNode(), this.dht.get(msg.getParameters()));
                server.reply(msg.getOrigin(), cMsg, comm);
            } catch (NoSuchElementException ex) {
                /* @todo Not sure why this exception is thrown here, checkup the system when tests are writtem*/
                logger.error("Element not found", ex);
            }
        } else {
            /**
             * Return a the K closest nodes to this content identifier
             * We create a NodeLookupReceiver and let this receiver handle this operation
             */
            NodeLookupMessage lkpMsg = new NodeLookupMessage(msg.getOrigin(), msg.getParameters().getKey());
            new NodeLookupReceiver(server, localNode, this.config).receive(lkpMsg, comm);
        }
    }

    @Override
    public void timeout(int comm) {
        // Simply ignore the timeout
    }
}
