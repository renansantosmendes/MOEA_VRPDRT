/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author renansantos
 */
public class WaitScreen {

    private JFrame frame;
    private JPanel panel;
    private JLabel label;

    public WaitScreen() {
        frame = new JFrame();
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        label = new JLabel("\n\nWait a minute...");
        panel.add(label);
        frame.setSize(200, 100);
        frame.add(panel, BorderLayout.NORTH);
        frame.setLocationRelativeTo(null);
        //frame.pack();
    }

    public void showScreen() {
        frame.setVisible(true);
    }

    public void hideScreen() {
        //frame.setVisible(false);
    }
}
