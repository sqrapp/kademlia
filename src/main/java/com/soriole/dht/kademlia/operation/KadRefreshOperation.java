package com.soriole.dht.kademlia.operation;

import com.soriole.dht.kademlia.KadConfiguration;
import com.soriole.dht.kademlia.KadServer;
import com.soriole.dht.kademlia.KademliaDHT;
import com.soriole.dht.kademlia.KademliaNode;

import java.io.IOException;

/**
 * An operation that handles refreshing the entire Kademlia Systems including buckets and content
 *
 * @author Joshua Kissoon
 * @since 20140306
 */
public class KadRefreshOperation implements Operation {

    private final KadServer server;
    private final KademliaNode localNode;
    private final KademliaDHT dht;
    private final KadConfiguration config;

    public KadRefreshOperation(KadServer server, KademliaNode localNode, KademliaDHT dht, KadConfiguration config) {
        this.server = server;
        this.localNode = localNode;
        this.dht = dht;
        this.config = config;
    }

    @Override
    public void execute() throws IOException {
        /* Run our BucketRefreshOperation to refresh buckets */
        new BucketRefreshOperation(this.server, this.localNode, this.config).execute();

        /* After buckets have been refreshed, we refresh content */
        new ContentRefreshOperation(this.server, this.localNode, this.dht, this.config).execute();
    }
}
