package protocol.message;

import java.io.*;
import java.util.List;

import com.google.gson.Gson;
import datatype.Header;

public class Headers {

    List<Header> headers;

    public Headers(List<Header> headers) {
        this.headers = headers;
    }

    public List<Header> getHeaders() {
        return headers;
    }

}
