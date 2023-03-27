package protocol.message;

import java.io.Serializable;

/**
 * A simplified <a href="https://reference.cash/protocol/network/messages/version">version</a> message just for network discovery
 */
public class Version implements Serializable {
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
