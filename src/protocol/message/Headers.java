package protocol.message;

import java.io.Serializable;
import java.util.List;
import datatype.Header;

public class Headers implements Serializable {

    List<Header> headers;

    public Headers(List<Header> headers) {
        this.headers = headers;
    }

    public List<Header> getHeaders() {
        return headers;
    }

}
