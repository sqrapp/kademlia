package com.soriole.dht.kademlia.message;

import com.soriole.dht.kademlia.node.Node;

public abstract class Message implements Streamable
{
	public Node origin;
    /**
     * The unique code for the message type, used to differentiate all messages
     * from each other. Since this is of <code>byte</code> type there can
     * be at most 256 different message types.
     *
     * @return byte A unique code representing the message type
     * */
    abstract public byte code();
}
