package protocol.message;

import com.google.gson.Gson;
import protocol.Payload;

import java.io.*;

/**
 * A simplified <a href="https://reference.cash/protocol/network/messages/version">version</a> message just for network discovery
 */
public class Version extends Payload implements Externalizable {
    private String localAddress;
    /*
    private String version;
    private String userAgent;
    ...
    In our demo, we only use this `version` message for network discovery.
    Therefore, other fields are omitted.
     */
    public Version(String localAddress) {
        this.localAddress = localAddress;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        out.writeUTF(json);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        String json = in.readUTF();
        Gson gson = new Gson();
        Version version = gson.fromJson(json, Version.class);
        this.localAddress = version.getLocalAddress();
    }
}
