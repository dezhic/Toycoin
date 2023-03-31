import protocol.datatype.Transaction;
import protocol.datatype.TxInput;
import protocol.datatype.TxOutput;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MiningTest {
    public static Blockchain blockchain; // store the Block
    public static int diff = 12;

    public static void main(String[]args) throws NoSuchAlgorithmException, IOException {

        blockchain = new Blockchain();
        LocalClient localClient = new LocalClient(blockchain);
        LocalServer localServer = new LocalServer(localClient);
        blockchain.setLocalClient(localClient);
        localServer.start();
        localClient.initialize();  // localClient needs to initialize AFTER localServer starts!

        // TODO: Given a List of Txs List<Tx>, select max. 30 of them and build a MercleTree (So, we need a merkle tree data structure to contain Txs).
        // ** Each block have its own Merkle tree
        // List to store the transaction obj
        List<Transaction> txMemPool = new ArrayList<>();
        // TODO: some data should be add into txMemPool...
        /** txMemPool.add(); */
        // Also, we need to have the hash val convert by each transaction
        List<String> txHashPool = new ArrayList<>();
        // function: calculate the hash with SHA-256
        for (Transaction tx : txMemPool) {
            String txHash = ProofOfWork.calculateHash(tx.toString());
            txHashPool.add(txHash); // put them into txPoolHash
        }
        // Then, put txHashPool into merkle tree function and generate
        MerkleTree merkleTrees = new MerkleTree(txHashPool);
//        merkleTrees.generateMerkleTreeRoot();
        // get root => data
//        System.out.println("root : " + merkleTrees.getRoot());


        if (System.getenv("PORT").equals("3888")) {
            blockchain.generateToAddress(5, "dummy_address");
        } else if (System.getenv("PORT").equals("3889")) {
            blockchain.sync();
        } else {
            blockchain.sync();
            blockchain.generateToAddress(2, "dummy_address");
            blockchain.sync();
        }
    }
}
