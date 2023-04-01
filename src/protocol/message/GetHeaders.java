package protocol.message;

import com.google.gson.Gson;

import java.io.*;
import java.util.List;

public class GetHeaders {
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
