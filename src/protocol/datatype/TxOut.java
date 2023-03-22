package protocol.datatype;

import java.io.Serializable;

public class TxOut implements Serializable {
    private long value;
    private String scriptPubKey;
}
