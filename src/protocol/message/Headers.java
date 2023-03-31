package protocol.message;

import java.io.*;
import java.util.List;

import com.google.gson.Gson;
import datatype.Header;
import protocol.Payload;

public class Headers extends Payload implements Externalizable {

    List<Header> headers;

    public Headers(List<Header> headers) {
        this.headers = headers;
    }

    public List<Header> getHeaders() {
        return headers;
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
        Headers headers = gson.fromJson(json, Headers.class);
        this.headers = headers.getHeaders();
    }
}
