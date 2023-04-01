package protocol.message;

import com.google.gson.Gson;

import java.io.*;
import java.util.List;

/**
 * <p>
 * Request the sequence of blocks that occur after a specific block.
 * If the specified block is on the server’s most-work chain,
 * the server responds with a set of up to 500 inv messages identifying
 * the next blocks on that chain. If the specified block is not on the
 * most-work chain, the server uses block information in the locator
 * structure to determine the fork point and provides inv messages from
 * that point.
 * </p>
 * <p>
 * If you just send in your last known hash, and it is off the main chain, the peer starts over at block #1.
 * </p>
 *
 * @see <a href="https://reference.cash/protocol/network/messages/getblocks">Request: Get Blocks (“getblocks”)</a>
 * @see <a href="https://reference.cash/protocol/network/messages/getheaders">Request: Get Headers (“getheaders”)</a>
 */
public class GetBlocks {
    private List<String> locator;
    private String hashStop;

    public GetBlocks(List<String> locator, String hashStop) {
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
