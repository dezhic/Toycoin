package gui;
import javax.swing.*;
import java.util.List;

public class MainGUI extends Thread {
    JList<String> blockList = new JList<>();
    public void run() {
        JFrame frame = new JFrame("My First GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        frame.getContentPane().add(blockList); // Adds Button to content pane of frame
        frame.setVisible(true);
    }

}
