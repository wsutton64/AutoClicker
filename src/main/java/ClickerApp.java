import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

    // Add necessary listeners and commands.
    public ClickerApp() {
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

    // This method checks the delay text field to see if a valid integer is set.
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

    // This method initializes a thread to run the autoclicker
    public void clicker() {
        Thread clickThread = new Thread(this);
        clickThread.start();
    }

    // The method to run the auto clicker in a separate thread.
    @Override
    public void run() {
        try {
            // On run, prevent the delaytext from being edited. Check which radiobutton is selected.
            delayTextField.setEditable(false);
            statusLabel.setText("Running");
            int activeMouseButton;
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
            // While isActive is true, the autoclicker will run.
            Robot robot = new Robot();
            while (isActive) {
                robot.mousePress(activeMouseButton);
                robot.mouseRelease(activeMouseButton);
                Thread.sleep(delay);
            }
            // When stopped, reenable delaytext editing
            delayTextField.setEditable(true);
            statusLabel.setText("Stopped");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Checks keys pressed. If key is F9, invert isActive and run the clicker if it is true.
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


