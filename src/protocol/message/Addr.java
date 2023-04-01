package protocol.message;

import java.io.*;
import java.util.List;

/**
 * @see <a href="https://reference.cash/protocol/network/messages/addr">addr</a>
 */
public class Addr {
    List<String> addresses;  // string format: host:port

    public Addr(List<String> addresses) {
        this.addresses = addresses;
    }

    public List<String> getAddresses() {
        return addresses;
    }

}
