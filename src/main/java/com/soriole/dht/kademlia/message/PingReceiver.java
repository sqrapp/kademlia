package com.soriole.dht.kademlia.message;

import com.soriole.dht.kademlia.KadServer;

import java.io.IOException;

public class PingReceiver implements Receiver{
    private KadServer server;
    public PingReceiver(KadServer server){
        this.server=server;
    }
    @Override
    public void receive(Message incoming, int conversationId) throws IOException {
        PingMessage msg= (PingMessage) incoming;
        System.out.println("Received Ping Message from -> "+incoming.sender.toDetailString());
        Message reply=new PongMessage(((PingMessage) incoming).random);
        server.reply(incoming.sender,reply,conversationId);
    }

    @Override
    public void timeout(int conversationId) throws IOException {

    }
}
