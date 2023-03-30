package com;

import protocol.datatype.ECDSAUtils;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

public class TransactionTest {
    public static void main(String[] args) throws Exception {
        // Generate a key pair for the person
        KeyPair keyPair = ECDSAUtils.getKeyPair();
        String publicKey = keyPair.getPublic().toString();
        // Test amount
        int amount = 100;

        // Create a transaction output
        TxOutput txOut1 = new TxOutput(publicKey, amount);
        List<TxOutput> txOutputs1 = new ArrayList<>();
        txOutputs1.add(txOut1);

        // Sign data using the private key
        String data = "Example data to sign";
        String signature = ECDSAUtils.signECDSA(keyPair.getPrivate(), data);

        // Create a transaction input
        TxInput txIn1 = new TxInput("txOutId1", 0, signature);

        // Create a transaction
        List<TxInput> txInputs = new ArrayList<>();
        txInputs.add(txIn1);
        List<TxOutput> txOutputs = new ArrayList<>(txOutputs1);
        Transaction transaction = new Transaction(txInputs, txOutputs);

        // Print transaction details
        System.out.println("Transaction ID: " + transaction.getId());
        System.out.println("Transaction Inputs: " + transaction.getTxInputs().size());
        System.out.println("Transaction Outputs: " + transaction.getTxOutputs().size());
    }
}