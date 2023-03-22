package protocol.datatype;

import java.io.Serializable;
import java.util.List;

public class Tx implements Serializable {
    private String txId;
    private List<TxIn> txIns;
    private List<TxOut> txOuts;

    // TODO: constructor, getTxId() aka. hash

}
