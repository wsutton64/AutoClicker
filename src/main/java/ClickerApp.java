import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

public class ClickerApp extends JFrame implements Runnable, NativeKeyListener {
    private JPanel MainPanel;
    private JLabel Title;
    private JButton startButton;
    private JTextField delayTextField;
    private JRadioButton leftRadioButton;
    private JRadioButton rightRadioButton;
    private JRadioButton middleRadioButton;
    private JLabel statusLabel;
    private ButtonGroup mouseButtonOptions;
    private Boolean isActive = false;
    private int delay = 500;
    private final int leftMouseButton = InputEvent.BUTTON1_DOWN_MASK;
    private final int middleMouseButton = InputEvent.BUTTON2_DOWN_MASK;
    private final int rightMouseButton = InputEvent.BUTTON3_DOWN_MASK;
    private int activeMouseButton;

    public ClickerApp() {
//        JFrame frame = new JFrame("Spooki's AutoClicker");
//        frame.setContentPane(new ClickerApp().MainPanel);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(400,250);
//        frame.pack();
//        frame.setVisible(true);
        leftRadioButton.setActionCommand("left");
        middleRadioButton.setActionCommand("middle");
        rightRadioButton.setActionCommand("right");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkDelay()) {
                    isActive = !isActive;
                    if (isActive) {
                        clicker();
                    }
                }
            }
        });

        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        } catch (NativeHookException e) {
            System.out.println("Native Hook crashed :(");
        }
    }

    public boolean checkDelay() {
        try {
            delay = Integer.parseInt(delayTextField.getText());
            if (delay <= 0) {
                throw new NumberFormatException();
            }
            return true;
        } catch (NumberFormatException exception) {
            delayTextField.setText("Invalid integer");
            return false;
        }
    }

    public void clicker() {
        Thread clickThread = new Thread(this);
        clickThread.start();
    }

    @Override
    public void run() {
        try {
            delayTextField.setEditable(false);
            statusLabel.setText("Running");
            switch (mouseButtonOptions.getSelection().getActionCommand()) {
                case "middle":
                    activeMouseButton = middleMouseButton;
                    break;
                case "right":
                    activeMouseButton = rightMouseButton;
                    break;
                default:
                    activeMouseButton = leftMouseButton;
            }
            Robot robot = new Robot();
            while (isActive) {
                robot.mousePress(activeMouseButton);
                robot.mouseRelease(activeMouseButton);
                Thread.sleep(delay);
            }
            delayTextField.setEditable(true);
            statusLabel.setText("Stopped");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F9) {
            if (checkDelay()) {
                isActive = !isActive;
                if (isActive) {
                    clicker();
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Spooki's AutoClicker");
        frame.setContentPane(new ClickerApp().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}


