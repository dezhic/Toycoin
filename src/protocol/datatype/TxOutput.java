package protocol.datatype;

import java.io.Serializable;
import java.security.PublicKey;

public class TxOutput implements Serializable {
    private long value;
    private String scriptPubKey;

    public TxOutput(long value, PublicKey scriptPubKey) {
        this.value = value;
        this.scriptPubKey = String.valueOf(scriptPubKey);
    }

    public long getValue() {
        return value;
    }

    public String getScriptPubKey() {
        return scriptPubKey;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void setScriptPubKey(String scriptPubKey) {
        this.scriptPubKey = scriptPubKey;
    }
}