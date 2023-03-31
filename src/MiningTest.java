import protocol.datatype.Transaction;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MiningTest {
    public static Blockchain blockchain; // store the Block
    public static int diff = 12;

    public static void main(String[]args) throws NoSuchAlgorithmException, IOException {

        LocalClient localClient = new LocalClient(null);
        LocalServer localServer = new LocalServer(localClient);
        localServer.start();
        localClient.initialize();  // localClient needs to initialize AFTER localServer starts!
        // Block information
        String previousHash = "0";
        String data = "Bruh";
        long timestamp = System.currentTimeMillis();

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
        merkleTrees.generateMerkleTreeRoot();
        // get root => data
        System.out.println("root : " + merkleTrees.getRoot());


        // declare the First block
        Block firstBlock = ProofOfWork.findBlock(null, 0, previousHash, timestamp, data, diff);
        blockchain = new Blockchain(firstBlock, localClient);

    }


}
