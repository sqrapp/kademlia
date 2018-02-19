package com.soriole.dht.kademlia.message;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

import org.springframework.web.servlet.RequestToViewNameTranslator;

import com.soriole.dht.kademlia.KadConfiguration;
import com.soriole.dht.kademlia.KadServer;
import com.soriole.dht.kademlia.KademliaNode;
import com.soriole.dht.kademlia.KademliaDHT;

/**
 * Handles creating messages and receivers
 *
 * @author Joshua Kissoon
 * @since 20140202
 */
public class MessageFactory implements KademliaMessageFactory
{

    private final KademliaNode localNode;
    private final KademliaDHT dht;
    private final KadConfiguration config;

    public MessageFactory(KademliaNode local, KademliaDHT dht, KadConfiguration config)
    {
        this.localNode = local;
        this.dht = dht;
        this.config = config;
    }

    @Override
    public Message createMessage(byte code, DataInputStream in, DatagramPacket packet) throws IOException
    {
    	Message message = null;
        switch (code)
        {
            case AcknowledgeMessage.MSG_CODE:
                message = new AcknowledgeMessage(in);
                break;
            case ConnectMessage.MSG_CODE:
                message = new ConnectMessage(in);
                break;
            case ContentMessage.MSG_CODE:
                message = new ContentMessage(in);
                break;
            case ContentLookupMessage.MSG_CODE:
                message = new ContentLookupMessage(in);
                break;
            case NodeLookupMessage.CODE:
                message = new NodeLookupMessage(in);
                break;
            case NodeReplyMessage.CODE:
                message = new NodeReplyMessage(in);
                break;
            case SimpleMessage.CODE:
                message = new SimpleMessage(in);
                break;
            case StoreContentMessage.CODE:
                message = new StoreContentMessage(in);
                break;
            default:
                //System.out.println(this.localNode + " - No Message handler found for message. Code: " + code);
                message = new SimpleMessage(in);

        }
        message.origin.setInetAddress(packet.getAddress());
        message.origin.setPort(packet.getPort());
        
        return message;
    }

    @Override
    public Receiver createReceiver(byte code, KadServer server)
    {
        switch (code)
        {
            case ConnectMessage.MSG_CODE:
                return new ConnectReceiver(server, this.localNode);
            case ContentLookupMessage.MSG_CODE:
                return new ContentLookupReceiver(server, this.localNode, this.dht, this.config);
            case NodeLookupMessage.CODE:
                return new NodeLookupReceiver(server, this.localNode, this.config);
            case StoreContentMessage.CODE:
                return new StoreContentReceiver(server, this.localNode, this.dht);
            default:
                //System.out.println("No receiver found for message. Code: " + code);
                return new SimpleReceiver();
        }
    }
}
