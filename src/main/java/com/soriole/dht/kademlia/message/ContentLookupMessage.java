package com.soriole.dht.kademlia.message;

import com.soriole.dht.kademlia.GetParameter;
import com.soriole.dht.kademlia.node.Node;
import com.soriole.dht.kademlia.util.serializer.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Messages used to send to another node requesting content.
 *
 * @author Joshua Kissoon
 * @since 20140226
 */
public class ContentLookupMessage extends Message {
    private static final Logger logger = LoggerFactory.getLogger(ContentLookupMessage.class);

    public static final byte MSG_CODE = 0x03;


    private GetParameter params;

    /**
     * @param origin The node where this lookup came from
     * @param params The parameters used to find the content
     */
    public ContentLookupMessage(Node origin, GetParameter params) {
        this.origin = origin;
        this.params = params;
    }

    public ContentLookupMessage(DataInputStream in) throws IOException {
        this.fromStream(in);
    }

    public GetParameter getParameters() {
        return this.params;
    }

    public Node getOrigin() {
        return this.origin;
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException {
        this.origin.toStream(out);

        /* Write the params to the stream */
        new JsonSerializer<GetParameter>().write(this.params, out);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException {
        this.origin = new Node(in);

        /* Read the params from the stream */
        try {
            this.params = new JsonSerializer<GetParameter>().read(in);
        } catch (ClassNotFoundException e) {
            logger.error("Error reading from stream", e);
        }
    }

    @Override
    public byte code() {
        return MSG_CODE;
    }

}
