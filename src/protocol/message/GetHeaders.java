package protocol.message;

import com.google.gson.Gson;
import protocol.Payload;

import java.io.*;
import java.util.List;

public class GetHeaders extends Payload implements Externalizable {
    private List<String> locator;
    private String hashStop;

    public GetHeaders(List<String> locator, String hashStop) {
        this.locator = locator;
        this.hashStop = hashStop;
    }

    public List<String> getLocator() {
        return locator;
    }

    public String getHashStop() {
        return hashStop;
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
        GetHeaders getHeaders = gson.fromJson(json, GetHeaders.class);
        this.hashStop = getHeaders.getHashStop();
        this.locator = getHeaders.getLocator();
    }
}
