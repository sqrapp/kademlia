package com.soriole.dht.kademlia.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PongMessage extends Message{
    public static final byte CODE=0x11;
    public int random;
    @Override
    public byte code() {
        return CODE;
    }
    public PongMessage(int reply){
        this.random =reply;
    }
    public PongMessage(DataInputStream in)throws IOException{
        this.fromStream(in);
    }
    @Override
    public void toStream(DataOutputStream out) throws IOException {
        out.writeInt(random);
    }

    @Override
    public void fromStream(DataInputStream out) throws IOException {
        random =out.readInt();
    }
}
