package com.soriole.dht.kademlia.operation;

import java.io.IOException;
import com.soriole.dht.kademlia.exceptions.RoutingException;

/**
 * An operation in the Kademlia routing protocol
 *
 * @author Joshua Kissoon
 * @created 20140218
 */
public interface Operation
{
    /**
     * Starts an operation and returns when the operation is finished
     *
     * @throws com.soriole.dht.kademlia.exceptions.RoutingException
     */
    public void execute() throws IOException, RoutingException;
}
