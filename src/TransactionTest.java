import datatype.Transaction;
import datatype.TxInput;
import datatype.TxOutput;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static util.ECDSAUtils.*;

public class TransactionTest {
    public static void main(String[] args) throws Exception {
        // Generate a key pair for the person
        KeyPair keyPair = getKeyPair();
        //Getting the private key from the key pair
        PrivateKey privateKey = keyPair.getPrivate();
        //Getting the public key from the key pair
        PublicKey publicKey = keyPair.getPublic();
        // Test amount
        int amount = 100;

        // Create a transaction output
        TxOutput txOut1 = new TxOutput(amount, publicKey);
        List<TxOutput> txOutputs1 = new ArrayList<>();
        txOutputs1.add(txOut1);

        // Sign data using the private key
        String data = "Example data to sign";
        String signature = signECDSA(privateKey, data);

        // Create a transaction input
        TxInput txIn1 = new TxInput("txOutId1", 0, signature);

        // Create a transaction
        List<TxInput> txInputs = new ArrayList<>();
        txInputs.add(txIn1);
        List<TxOutput> txOutputs = new ArrayList<>(txOutputs1);
        Transaction transaction = new Transaction(txInputs, txOutputs);

        // Print transaction details
        System.out.println("Transaction ID: " + transaction.getId());
        System.out.println("Transaction Inputs: " + transaction.getTxIns().size());
        System.out.println("Transaction Outputs: " + transaction.getTxOuts().size());

        verifyECDSA(publicKey, signature, data);
    }
}