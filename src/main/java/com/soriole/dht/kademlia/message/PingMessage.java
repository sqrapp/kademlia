package com.soriole.dht.kademlia.message;

import com.soriole.dht.kademlia.routing.KademliaRoutingTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

public class PingMessage extends Message implements Receiver{
    private static Logger logger=LoggerFactory.getLogger(PingMessage.class);
    KademliaRoutingTable table;
    public static final byte CODE=0x10;
    public int random;
    public PingMessage(KademliaRoutingTable table){
        this.table=table;
    }
    public PingMessage(DataInputStream is){
        try {
            this.fromStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public byte code() {
        return CODE;
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException {
        random=new Random().nextInt();
        out.write(random);
    }

    @Override
    public void fromStream(DataInputStream out) throws IOException {
        random=out.readInt();
    }

    @Override
    public void receive(Message incoming, int conversationId) throws IOException {
        if(((PingMessage)incoming).random==random){
            logger.info("Ping message acknowledged from "+ sender.toDetailString());
        }
        else{

        }
    }

    @Override
    public void timeout(int conversationId) throws IOException {
        table.setUnresponsiveContact(sender);
    }
}
