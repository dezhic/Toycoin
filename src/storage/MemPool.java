package storage;

import datatype.Transaction;
import gui.GUI;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemPool {
    private static MemPool instance = null;
    private static final Object lock = new Object();

    private Map<String, Transaction> txMap = new ConcurrentHashMap<>();

    public static MemPool getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new MemPool();
                }
            }
        }
        return instance;
    }

    public void add(Transaction tx) {
        txMap.put(tx.getId(), tx);
        updateGui();
    }

    public void remove(Transaction tx) {
        txMap.remove(tx.getId());
        updateGui();
    }

    public void remove(String txId) {
        txMap.remove(txId);
        updateGui();
    }

    public Transaction get(String txId) {
        return txMap.get(txId);
    }

    public void updateGui() {
        GUI gui = GUI.getInstance();
        gui.updateMemPoolTable(new ArrayList<>(txMap.keySet()));
    }
}
