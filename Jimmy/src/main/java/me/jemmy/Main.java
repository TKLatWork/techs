package me.jemmy;

import javax.swing.*;
import java.awt.Container;
import java.awt.Component;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JButtonOperator;

public class Main {
    
    public void runSwing(){
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hello World");
            JButton button = new JButton("Click Me");
            button.addActionListener(e -> System.out.println("Button clicked!"));
            frame.add(button);
            frame.setSize(300, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            frame.add(new JLabel("Welcome to Jemmy!"));
        });
    }
    
    public void walkFrame(){
        try {
            Thread.sleep(1000);
            // Wait a bit for GUI to show
            System.out.println(
                    "Waiting for the frame to be visible...\n"
            );
            JFrameOperator fo = new JFrameOperator("Hello World");
            Container cont = fo.getContentPane();
            walk(cont, fo);
            System.out.println("Finished walking the frame.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void walk(Container cont, JFrameOperator fo) {
        for (Component c : cont.getComponents()) {
            System.out.println(c.getClass().getName() + ": " + c.getName());
            if (c instanceof JButton) {
                JButtonOperator bo = new JButtonOperator(fo, ((JButton) c).getText());
                bo.push();
                System.out.println("Clicked button: " + c.getName());
            }
            if (c instanceof Container) {
                walk((Container) c, fo);
            }
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.runSwing();
        main.walkFrame();
    }
    
}
