package org.tigris.gef.demo;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * This "demo" class is pretty pointless but I have modified it to be
 * a little more "modern" in code/design.  Not sure why they originally 
 * included this when they could have demo'd JGraphFrame or something?
 * Quite frankly, I may delete this eventually (or completely change it).
 * 
 * @author rwellman
 * @since [[]]<since
 *
 */
public class Notify extends JFrame {

    private static final long serialVersionUID = -3943708263259356434L;

    public Notify() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                dispose();
            }

            public void windowClosed(WindowEvent event) {
                System.exit(0);
            }
        });

        JTextArea text = new JTextArea(
                "GEF is a pure library to enable graph modelling, not an application.\nSee http://gef.tigris.org and http://gefdemo.tigris.org to see details\nof how to use GEF and see example applications.");
        text.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        text.setEditable(false);

        JPanel p = new JPanel(new GridBagLayout());
        p.add(text);

        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());
        cont.add(p);

        setBounds(0, 0, 400, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(
            () -> { new Notify(); }
        );
    }

}
