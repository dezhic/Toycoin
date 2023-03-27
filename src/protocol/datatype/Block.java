package protocol.datatype;

import java.io.Serializable;

public class Block implements Serializable {
    private String blockId;
    private String prevBlockId;
    private String merkleRoot;
    private long timestamp;
    private int bits;
    private int nonce;
    private Tx[] txs;
}
