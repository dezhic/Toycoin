package protocol.message;

import com.google.gson.Gson;
import protocol.Payload;

import java.io.*;
import java.util.List;

/**
 * @see <a href="https://reference.cash/protocol/network/messages/addr">addr</a>
 */
public class Addr extends Payload implements Externalizable {
    List<String> addresses;  // string format: host:port

    public Addr(List<String> addresses) {
        this.addresses = addresses;
    }

    public List<String> getAddresses() {
        return addresses;
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
        Addr addr = gson.fromJson(json, Addr.class);
        this.addresses = addr.getAddresses();
    }
}
