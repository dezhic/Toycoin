package storage;

import datatype.Transaction;
import datatype.TxOutput;
import util.ECDSAUtils;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A collection of public and private keys.
 * Responsible for generating new keys, and signing transactions.
 */
public class Wallet {

    private Map<String, String> keys;  // public key -> private key
    private Map<String, List<TxOutput>> utxos;  // public key -> unspent outputs

    public Wallet() {
        keys = new HashMap<>();
        utxos = new HashMap<>();
    }

    /**
     * Generates ECDSA key pair and stores them in this wallet.
     */
    public void generateKey() {
        KeyPair keyPair = ECDSAUtils.getKeyPair();
        String publicKey = String.valueOf(keyPair.getPublic());
        String privateKey = String.valueOf(keyPair.getPrivate());
        keys.put(publicKey, privateKey);
    }

    /**
     * return a list of public and private keys with the sum of their unspent output values
     * each item formatted as "publicKey:privateKey:balance"
     */
    public List<String> getKeysWithBalance() {
        // Get a list of public keys and private keys from Map keys
        return keys.entrySet().stream()
                .map(entry -> {
                    String publicKey = entry.getKey();
                    String privateKey = entry.getValue();
                    // Get the sum of unspent outputs for this public key
                    if (!utxos.containsKey(publicKey)) {
                        return publicKey + ":" + privateKey + ":0";
                    } else {
                        long balance = utxos.get(publicKey).stream()
                                .mapToLong(TxOutput::getValue)
                                .sum();
                        return publicKey + ":" + privateKey + ":" + balance;
                    }
                })
                .collect(Collectors.toList());
    }

    public void addUtxos(String publicKey, List<TxOutput> utxos) {
        // add or concatenate to existing list
        if (this.utxos.containsKey(publicKey)) {
            this.utxos.get(publicKey).addAll(utxos);
        } else {
            // add a mutable copy of the list
            List<TxOutput> copy = new LinkedList<>(utxos);
            this.utxos.put(publicKey, copy);
        }
    }

    public void removeUtxos(String publicKey, List<TxOutput> utxos) {
        // remove from existing list
        if (this.utxos.containsKey(publicKey)) {
            this.utxos.get(publicKey).removeAll(utxos);
        }
    }

    public Transaction createTransaction(String from, String to, long value) {
        // TODO
        return null;
    }

}
