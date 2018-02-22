package com.soriole.dht.kademlia.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import com.soriole.dht.kademlia.JKademliaStorageEntry;
import com.soriole.dht.kademlia.node.KademliaId;
import com.soriole.dht.kademlia.node.Node;
import com.soriole.dht.kademlia.util.serializer.JsonSerializer;

/**
 * A StoreContentMessage used to send a store message to a node
 *
 * @author Joshua Kissoon
 * @since 20140225
 */
public class StoreContentMessage extends Message
{

    public static final byte CODE = 0x08;

    private JKademliaStorageEntry content;

    /**
     * @param origin  Where the message came from
     * @param content The content to be stored
     *
     */
    public StoreContentMessage(Node origin, JKademliaStorageEntry content)
    {
        this.content = content;
        this.sender = origin;
    }

    public StoreContentMessage(DataInputStream in) throws IOException
    {
        this.fromStream(in);
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        this.sender.getNodeId().toStream(out);


        /* Serialize the KadContent, then send it to the stream */
        new JsonSerializer<JKademliaStorageEntry>().write(content, out);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
        this.sender.setNodeId(new KademliaId(in));
        try
        {
            this.content = new JsonSerializer<JKademliaStorageEntry>().read(in);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public JKademliaStorageEntry getContent()
    {
        return this.content;
    }

    @Override
    public byte code()
    {
        return CODE;
    }

    @Override
    public String toString()
    {
        return "StoreContentMessage[sender=" + sender + ",content=" + content + "]";
    }
}
