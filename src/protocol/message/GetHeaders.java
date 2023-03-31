package protocol.message;

import java.io.Serializable;
import java.util.List;

public class GetHeaders implements Serializable {
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
}
