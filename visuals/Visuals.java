package Visuals;

import javax.swing.*;

public class Visuals {
    private double scaleFactor;
    private JFrame frame;

    public Visuals(double sf) {
        scaleFactor = sf;
        frame = new JFrame("Bin Packing Visualization (1px = " + 1 / scaleFactor + "mm)");
        frame.setSize((int)(1000 * scaleFactor), (int)(1000 * scaleFactor));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void visualize() {
        frame.setVisible(true);
    }
}