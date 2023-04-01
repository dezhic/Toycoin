package protocol.message;

import com.google.gson.Gson;

import java.io.*;

/**
 * A simplified <a href="https://reference.cash/protocol/network/messages/version">version</a> message just for network discovery
 */
public class Version {
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

}
