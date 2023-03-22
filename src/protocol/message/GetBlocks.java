package protocol.message;

import java.io.Serializable;

/**
 * Request the sequence of blocks that occur after a specific block.
 * If the specified block is on the server’s most-work chain,
 * the server responds with a set of up to 500 inv messages identifying
 * the next blocks on that chain. If the specified block is not on the
 * most-work chain, the server uses block information in the locator
 * structure to determine the fork point and provides inv messages from
 * that point.
 *
 * @see <a href="https://reference.cash/protocol/network/messages/getblocks">Request: Get Blocks (“getblocks”)</a>
 */
public class GetBlocks implements Serializable {
    private String locator;
    private String hashStop;
}
