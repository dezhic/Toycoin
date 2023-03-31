package gui;
import datatype.Block;

import javax.swing.*;
import java.util.List;

public class GUI extends Thread {
    JFrame frame;
    JList<String> blockList;
    public GUI(String name) {
        frame = new JFrame(name);
        blockList = new JList<>();
    }
    public void run() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        frame.getContentPane().add(blockList); // Adds Button to content pane of frame
        frame.setVisible(true);
    }

    public void updateBlockList(List<Block> blockList) {
        this.blockList.setListData(blockList.stream().map(Block::getHash).toArray(String[]::new));
//        frame.setVisible(true);
    }

}
